/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.soikonomakis.rxfirebase;


import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.soikonomakis.rxfirebase.FirebaseChildEvent.EventType;
import com.soikonomakis.rxfirebase.exceptions.FirebaseDatabaseError;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

/**
 * The class is used as wrapper to firebase functionlity with
 * RxJava
 */
public class RxFirebase {

    private static RxFirebase instance;

    /**
     * Singleton
     *
     * @return {@link RxFirebase}
     */
    public static synchronized RxFirebase getInstance() {
        if (instance == null) {
            instance = new RxFirebase();
        }
        return instance;
    }

    //Prevent constructor initialisation
    private RxFirebase() {

    }

    /**
     * Attempts to authenticate to Firebase with an OAuth token from a provider supported by Firebase
     * Login. This method only works for providers that only require a 'access_token' as a parameter
     */
    public Observable<AuthResult> observeAuthWithCredential(final AuthCredential authCredential) {
        return Observable.create(new Observable.OnSubscribe<AuthResult>() {
            @Override
            public void call(final Subscriber<? super AuthResult> subscriber) {
                Task<AuthResult> task = FirebaseAuth.getInstance().signInWithCredential(authCredential);
                configureAuthResultListener(subscriber, task);

            }

            public Observable<AuthResult> observeAuthAnonymous() {
                return Observable.create(new Observable.OnSubscribe<AuthResult>() {
                    @Override
                    public void call(final Subscriber<? super AuthResult> subscriber) {
                        Task<AuthResult> authResultTask = FirebaseAuth.getInstance().signInAnonymously();
                        configureAuthResultListener(subscriber, authResultTask);
                    }
                });
            }
        });
    }

    /**
     * This methods observes a firebase query and returns back
     * an Observable of the DataSnapshot
     * when the firebase client uses a ValueEventListener
     */
    public Observable<DataSnapshot> observeValueEvent(final Query ref) {
        return Observable.create(new Observable.OnSubscribe<DataSnapshot>() {
            @Override
            public void call(final Subscriber<? super DataSnapshot> subscriber) {
                final ValueEventListener listener = ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        subscriber.onNext(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        attachErrorHandler(subscriber, error);
                    }
                });

                // When the subscription is cancelled, remove the listener
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        ref.removeEventListener(listener);
                    }
                }));
            }
        });
    }

    /**
     * This methods observes a firebase query and returns back ONCE
     * an Observable of the DataSnapshot
     * when the firebase client uses a ValueEventListener
     */
    public Observable<DataSnapshot> observeSingleValue(final Query ref) {
        return Observable.create(new Observable.OnSubscribe<DataSnapshot>() {
            @Override
            public void call(final Subscriber<? super DataSnapshot> subscriber) {
                final ValueEventListener listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        subscriber.onNext(dataSnapshot);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        attachErrorHandler(subscriber, error);
                    }
                };

                ref.addListenerForSingleValueEvent(listener);

                // When the subscription is cancelled, remove the listener
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        ref.removeEventListener(listener);
                    }
                }));
            }
        });
    }

    /**
     * This methods observes a firebase query and returns back
     * an Observable of the DataSnapshot
     * when the firebase client uses a ChildEventListener
     */
    public Observable<FirebaseChildEvent> observeChildEvent(final Query ref) {
        return Observable.create(new Observable.OnSubscribe<FirebaseChildEvent>() {
            @Override
            public void call(final Subscriber<? super FirebaseChildEvent> subscriber) {
                final ChildEventListener childEventListener =
                        ref.addChildEventListener(new ChildEventListener() {

                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                                subscriber.onNext(
                                        new FirebaseChildEvent(dataSnapshot, previousChildName, EventType.ADDED));
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                                subscriber.onNext(
                                        new FirebaseChildEvent(dataSnapshot, previousChildName, EventType.CHANGED));
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                subscriber.onNext(new FirebaseChildEvent(dataSnapshot, EventType.REMOVED));
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                                subscriber.onNext(
                                        new FirebaseChildEvent(dataSnapshot, previousChildName, EventType.MOVED));
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                attachErrorHandler(subscriber, error);
                            }


                        });
                // this is used to remove the listener when the subscriber is
                // cancelled (unsubscribe)
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        ref.removeEventListener(childEventListener);
                    }
                }));
            }
        });
    }

    /**
     * Creates an observable only for the child added method
     */
    public Observable<FirebaseChildEvent> observeChildAdded(Query ref) {
        return observeChildEvent(ref).filter(filterChildEvent(EventType.ADDED));
    }

    /**
     * Creates an observable only for the child changed method
     */
    public Observable<FirebaseChildEvent> observeChildChanged(Query ref) {
        return observeChildEvent(ref).filter(filterChildEvent(EventType.CHANGED));
    }

    /**
     * Creates an observable only for the child removed method
     */
    public Observable<FirebaseChildEvent> observeChildRemoved(Query ref) {
        return observeChildEvent(ref).filter(filterChildEvent(EventType.REMOVED));
    }

    /**
     * Creates an observable only for the child removed method
     */
    public Observable<FirebaseChildEvent> observeChildMoved(Query ref) {
        return observeChildEvent(ref).filter(filterChildEvent(EventType.MOVED));
    }

    /**
     * Functions which filters a stream of {@link Observable} according to firebase
     * child event type
     */
    private Func1<FirebaseChildEvent, Boolean> filterChildEvent(final EventType type) {
        return new Func1<FirebaseChildEvent, Boolean>() {
            @Override
            public Boolean call(FirebaseChildEvent firebaseChildEvent) {
                return firebaseChildEvent.getEventType() == type;
            }
        };
    }


    private <T> void attachErrorHandler(Subscriber<T> subscriber, DatabaseError firebaseError) {
        subscriber.onError(new FirebaseDatabaseError(firebaseError.getCode(), firebaseError.getMessage() + firebaseError.getDetails()));
    }

    private void configureAuthResultListener(final Subscriber<? super AuthResult> subscriber, Task<AuthResult> task) {
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                subscriber.onError(e);
            }
        })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        subscriber.onNext(authResult);
                        subscriber.onCompleted();
                    }
                });
    }
}
