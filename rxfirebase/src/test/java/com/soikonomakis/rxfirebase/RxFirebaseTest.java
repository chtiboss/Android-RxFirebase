package com.soikonomakis.rxfirebase;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RxFirebaseTest extends ApplicationTestCase {

  private RxFirebase spyRxFirebase;
  @Mock private DatabaseReference mockRef;
  @Mock private DataSnapshot mockDataSnapshot;
  @Mock private AuthResult mockAuthData;
  @Mock private FirebaseChildEvent mockFirebaseChildEvent;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);

    RxFirebase rxFirebase = RxFirebase.getInstance();
    spyRxFirebase = spy(rxFirebase);
  }

  @After public void destroy() throws NoSuchFieldException, IllegalAccessException {
    resetSingleton(RxFirebase.class);
    spyRxFirebase = null;
  }

//  @Test public void testObserveOauthWithToken() throws InterruptedException {
//    when(spyRxFirebase.observeAuthWithOauthToken(mockRef, "test", "google")).thenReturn(
//        Observable.just(mockAuthData));
//
//    TestSubscriber<AuthData> testSubscriber = new TestSubscriber<>();
//    spyRxFirebase.observeAuthWithOauthToken(mockRef, "test", "google")
//        .subscribeOn(Schedulers.immediate())
//        .subscribe(testSubscriber);
//
//    testSubscriber.assertNoErrors();
//    testSubscriber.assertValueCount(1);
//    testSubscriber.assertReceivedOnNext(Collections.singletonList(mockAuthData));
//    testSubscriber.assertCompleted();
//    testSubscriber.unsubscribe();
//
//    verify(spyRxFirebase).observeAuthWithOauthToken(mockRef, "test", "google");
//  }
//
  @Test public void testObserveValue() throws InterruptedException {
    when(spyRxFirebase.observeValueEvent(mockRef)).thenReturn(Observable.just(mockDataSnapshot));

    TestSubscriber<DataSnapshot> testSubscriber = new TestSubscriber<>();
    spyRxFirebase.observeValueEvent(mockRef)
        .subscribeOn(Schedulers.immediate())
        .subscribe(testSubscriber);

    testSubscriber.assertNoErrors();
    testSubscriber.assertValueCount(1);
    testSubscriber.assertReceivedOnNext(Collections.singletonList(mockDataSnapshot));
    testSubscriber.assertCompleted();
    testSubscriber.unsubscribe();

    verify(spyRxFirebase).observeValueEvent(mockRef);
  }

  @Test public void testObserveChildValue() {
    when(spyRxFirebase.observeChildEvent(mockRef)).thenReturn(
        Observable.just(mockFirebaseChildEvent));

    TestSubscriber<FirebaseChildEvent> testSubscriber = new TestSubscriber<>();
    spyRxFirebase.observeChildEvent(mockRef)
        .subscribeOn(Schedulers.immediate())
        .subscribe(testSubscriber);

    testSubscriber.assertNoErrors();
    testSubscriber.assertValueCount(1);
    testSubscriber.assertReceivedOnNext(Collections.singletonList(mockFirebaseChildEvent));
    testSubscriber.assertCompleted();
    testSubscriber.unsubscribe();

    verify(spyRxFirebase).observeChildEvent(mockRef);
  }

  @Test public void testObserveSingleValue() {
    when(spyRxFirebase.observeSingleValue(mockRef)).thenReturn(Observable.just(mockDataSnapshot));

    TestSubscriber<DataSnapshot> testSubscriber = new TestSubscriber<>();
    spyRxFirebase.observeSingleValue(mockRef)
        .subscribeOn(Schedulers.immediate())
        .subscribe(testSubscriber);

    testSubscriber.assertNoErrors();
    testSubscriber.assertValueCount(1);
    testSubscriber.assertReceivedOnNext(Collections.singletonList(mockDataSnapshot));
    testSubscriber.assertCompleted();
    testSubscriber.unsubscribe();

    verify(spyRxFirebase).observeSingleValue(mockRef);
  }

  @Test public void testObserveChildAdded() {
    mockFirebaseChildEvent.setEventType(FirebaseChildEvent.EventType.ADDED);
    when(spyRxFirebase.observeChildAdded(mockRef)).thenReturn(
        Observable.just(mockFirebaseChildEvent));

    TestSubscriber<FirebaseChildEvent> testSubscriber = new TestSubscriber<>();
    spyRxFirebase.observeChildAdded(mockRef)
        .subscribeOn(Schedulers.immediate())
        .subscribe(testSubscriber);

    testSubscriber.assertNoErrors();
    testSubscriber.assertValueCount(1);
    testSubscriber.assertReceivedOnNext(Collections.singletonList(mockFirebaseChildEvent));
    testSubscriber.assertCompleted();
    testSubscriber.unsubscribe();

    verify(spyRxFirebase, atMost(2)).observeChildAdded(mockRef);
  }

  @Test public void testObserveChildRemoved() {
    mockFirebaseChildEvent.setEventType(FirebaseChildEvent.EventType.REMOVED);
    when(spyRxFirebase.observeChildRemoved(mockRef)).thenReturn(
        Observable.just(mockFirebaseChildEvent));

    TestSubscriber<FirebaseChildEvent> testSubscriber = new TestSubscriber<>();
    spyRxFirebase.observeChildRemoved(mockRef)
        .subscribeOn(Schedulers.immediate())
        .subscribe(testSubscriber);

    testSubscriber.assertNoErrors();
    testSubscriber.assertValueCount(1);
    testSubscriber.assertReceivedOnNext(Collections.singletonList(mockFirebaseChildEvent));
    testSubscriber.assertCompleted();
    testSubscriber.unsubscribe();

    verify(spyRxFirebase, atMost(2)).observeChildRemoved(mockRef);
  }

  @Test public void testObserveChildChanged() {
    mockFirebaseChildEvent.setEventType(FirebaseChildEvent.EventType.CHANGED);
    when(spyRxFirebase.observeChildChanged(mockRef)).thenReturn(
        Observable.just(mockFirebaseChildEvent));

    TestSubscriber<FirebaseChildEvent> testSubscriber = new TestSubscriber<>();
    spyRxFirebase.observeChildChanged(mockRef)
        .subscribeOn(Schedulers.immediate())
        .subscribe(testSubscriber);

    testSubscriber.assertNoErrors();
    testSubscriber.assertValueCount(1);
    testSubscriber.assertReceivedOnNext(Collections.singletonList(mockFirebaseChildEvent));
    testSubscriber.assertCompleted();
    testSubscriber.unsubscribe();

    verify(spyRxFirebase, atMost(2)).observeChildChanged(mockRef);
  }

  @Test public void testObserveChildMoved() {
    mockFirebaseChildEvent.setEventType(FirebaseChildEvent.EventType.MOVED);
    when(spyRxFirebase.observeChildMoved(mockRef)).thenReturn(
        Observable.just(mockFirebaseChildEvent));

    TestSubscriber<FirebaseChildEvent> testSubscriber = new TestSubscriber<>();
    spyRxFirebase.observeChildMoved(mockRef)
        .subscribeOn(Schedulers.immediate())
        .subscribe(testSubscriber);

    testSubscriber.assertNoErrors();
    testSubscriber.assertValueCount(1);
    testSubscriber.assertReceivedOnNext(Collections.singletonList(mockFirebaseChildEvent));
    testSubscriber.assertCompleted();
    testSubscriber.unsubscribe();

    verify(spyRxFirebase, atMost(2)).observeChildMoved(mockRef);
  }
}
