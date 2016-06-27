package com.soikonomakis.rxfirebase;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import rx.Observable;
import rx.Subscriber;

public class RxFirebaseStorage {

    /**
     * taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
     *
     * @param url   "gs://<your-bucket-name>/path/file" <your-bucket-name> is accessible by R.string.google_storage_bucket
     * @param bytes
     */
    public Observable<UploadTask.TaskSnapshot> uploadBytes(final String url, final byte[] bytes) {
        return Observable.create(new Observable.OnSubscribe<UploadTask.TaskSnapshot>() {
            @Override
            public void call(final Subscriber<? super UploadTask.TaskSnapshot> subscriber) {
                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                UploadTask uploadTask = storageRef.putBytes(bytes);
                configureUploadListener(subscriber, uploadTask);
            }
        });
    }

    public Observable<UploadTask.TaskSnapshot> uploadBytes(final String url, final byte[] bytes, StorageMetadata metadata) {
        return uploadBytes(url, bytes, null);
    }

    public Observable<UploadTask.TaskSnapshot> uploadStream(final String url, final InputStream stream) {
        return uploadStream(url, stream, null);
    }

    public Observable<UploadTask.TaskSnapshot> uploadStream(final String url, final InputStream stream, final StorageMetadata metadata) {
        return Observable.create(new Observable.OnSubscribe<UploadTask.TaskSnapshot>() {
            @Override
            public void call(final Subscriber<? super UploadTask.TaskSnapshot> subscriber) {
                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                UploadTask uploadTask = storageRef.putStream(stream, metadata);
                configureUploadListener(subscriber, uploadTask);
            }
        });
    }

    public Observable<UploadTask.TaskSnapshot> uploadFile(final String url, final File file) {
        return uploadFile(url, file);
    }
    public Observable<UploadTask.TaskSnapshot> uploadFile(final String url, final File file, final StorageMetadata metadata) {
        return Observable.create(new Observable.OnSubscribe<UploadTask.TaskSnapshot>() {
            @Override
            public void call(final Subscriber<? super UploadTask.TaskSnapshot> subscriber) {
                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                UploadTask uploadTask = storageRef.putFile(Uri.fromFile(file), metadata);
                configureUploadListener(subscriber, uploadTask);
            }
        });
    }


    private void configureUploadListener(final Subscriber<? super UploadTask.TaskSnapshot> subscriber, UploadTask uploadTask) {
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                subscriber.onNext(taskSnapshot);
                subscriber.onCompleted();
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                subscriber.onError(e);
            }
        });
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                subscriber.onNext(taskSnapshot);
            }
        });
    }

    public Observable<UploadTask.TaskSnapshot> uploadFile(final String url, Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        return uploadBytes(url, data);
    }


}
