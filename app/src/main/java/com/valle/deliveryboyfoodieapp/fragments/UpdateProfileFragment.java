package com.valle.deliveryboyfoodieapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.valle.deliveryboyfoodieapp.R;
import com.valle.deliveryboyfoodieapp.base.BaseFragment;
import com.valle.deliveryboyfoodieapp.models.LoginModel;
import com.valle.deliveryboyfoodieapp.models.UpdateProfileModel;
import com.valle.deliveryboyfoodieapp.network.Apis;
import com.valle.deliveryboyfoodieapp.network.NetworkResponceListener;
import com.valle.deliveryboyfoodieapp.prefs.SharedPrefModule;
import com.valle.deliveryboyfoodieapp.utils.CommonUtils;
import com.valle.deliveryboyfoodieapp.utils.ImageFilePath;
import com.valle.deliveryboyfoodieapp.utils.RoundedImageView;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UpdateProfileFragment extends BaseFragment implements NetworkResponceListener {

    private static final int REQUEST_CAMERA = 5;
    private static final int SELECT_FILE = 6;
    private String imageFilePath;
    private String userChoosenTask;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 12;
    private File file;
    private static final String TAG = "UpdateProfileFragment";

    @BindView(R.id.ivProfileImage)
    RoundedImageView ivProfileImage;

    @BindView(R.id.etDeliveryBName)
    AppCompatEditText etDeliveryBName;

    @BindView(R.id.etDeliveryBoyEmailId)
    AppCompatEditText etDeliveryBoyEmailId;

    @BindView(R.id.etDeliveryBoyMobileNumber)
    AppCompatEditText etDeliveryBoyMobileNumber;

    @BindView(R.id.tvUserName)
    AppCompatTextView tvUserName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(null);
        bindView(this, view);
        setData();
    }

    private void setData() {
        String userLoginResponseData = new SharedPrefModule(getActivity()).getUserLoginResponseData();
        if (TextUtils.isEmpty(userLoginResponseData)) {
            return;
        }

        LoginModel.responseData.UserInfoData loginModel =
                new Gson().fromJson(userLoginResponseData, LoginModel.responseData.UserInfoData.class);
        tvUserName.setText(loginModel.Full_Name);
        etDeliveryBName.setText(loginModel.Full_Name);
        etDeliveryBoyMobileNumber.setText(loginModel.Mobile);
        etDeliveryBoyEmailId.setText(loginModel.Email);

        try {
            if (!TextUtils.isEmpty(loginModel.Profile_Image)) {
                Glide.with(getActivity()).load(Apis.API_URL_FILE + loginModel.Profile_Image).placeholder(getResources().getDrawable(R.drawable.upload_image)).into(ivProfileImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("CheckResult")
    private void compressImage(File filee) {
        new Compressor(getActivity())
                .compressToFileAsFlowable(filee)
                .subscribeOn(Schedulers.io())
                .subscribe(file1 -> {
                    file = file1;
                }, throwable -> throwable.printStackTrace());
    }

    @OnClick(R.id.tvUpdateProfile)
    void OnClicktvUpdateProfile() {

        String restaurantName = etDeliveryBName.getText().toString();
        String restaurantEmail = etDeliveryBoyEmailId.getText().toString();
        String mobileNumber = etDeliveryBoyMobileNumber.getText().toString();

        if (TextUtils.isEmpty(restaurantName)) {
            etDeliveryBName.setError(getResources().getString(R.string.enter_your_name));
            etDeliveryBName.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(restaurantEmail)) {
            etDeliveryBoyEmailId.setError(getResources().getString(R.string.plz_entr_your_email));
            etDeliveryBoyEmailId.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(mobileNumber)) {
            etDeliveryBoyMobileNumber.setError(getResources().getString(R.string.plz_entr_your_mob_number));
            etDeliveryBoyMobileNumber.setFocusable(true);
            return;
        }
        MultipartBody.Part body = null;
        if (file != null) {
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body = MultipartBody.Part.createFormData("Profile_Image", file.getName(), requestFile);
        }
        RequestBody userId =
                RequestBody.create(MediaType.parse("multipart/form-data"), new SharedPrefModule(getActivity()).getUserId());

        RequestBody name =
                RequestBody.create(MediaType.parse("multipart/form-data"), restaurantName);

        RequestBody email =
                RequestBody.create(MediaType.parse("multipart/form-data"), restaurantEmail);

        RequestBody phone =
                RequestBody.create(MediaType.parse("multipart/form-data"), mobileNumber);

        showProgressDialog(getActivity());
        makeHttpCallToUploadImageData(this, Apis.EDIT_PROFILE, getRetrofitInterfaceToUploadImageData().updateProfile(body, userId, name, email, phone));
    }

    @OnClick(R.id.flUpdateImage)
    void OnClickbtChooseImageFile() {
        askPermissionCamera();
    }

    private void openImageCaptureDialog() {
        final CharSequence[] items = {"Tomar foto", "Elige de la biblioteca",
                "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Añadir foto!");
        builder.setItems(items, (dialog, item) -> {

            if (items[item].equals("Tomar foto")) {
                userChoosenTask = "Take Photo";
                cameraIntent();
            } else if (items[item].equals("Elige de la biblioteca")) {
                userChoosenTask = "Choose from Library";
                galleryIntent();
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                }

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String picturePath = ImageFilePath.getPath(getActivity().getApplicationContext(), selectedImageUri);
        file = new File(picturePath);
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                ivProfileImage.setImageBitmap(bm);
                compressImage(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) BitmapFactory.decodeFile(imageFilePath);
        file = new File(imageFilePath);
        try {

            ivProfileImage.setImageBitmap(thumbnail);
            compressImage(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void galleryIntent() {
        if (CommonUtils.checkPermission(getActivity())) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
        }
    }

    private void cameraIntent() {
        if (CommonUtils.checkPermissionCamera(getActivity())) {
            Intent pictureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            if (pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                //Create a file to store the image
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getActivity(), "com.valle.deliveryboyfoodieapp.provider", photoFile);
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            photoURI);
                    startActivityForResult(pictureIntent,
                            REQUEST_CAMERA);
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void askPermissionCamera() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                openImageCaptureDialog();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
            }
        };

        TedPermission.with(getActivity())
                .setPermissionListener(permissionlistener)
                .setDeniedTitle("Permission denied")
                .setDeniedMessage(
                        "If you reject permission,you can not use getActivity() service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setGotoSettingButtonText("Go to setting")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(getActivity());

        switch (url) {
            case Apis.EDIT_PROFILE:
                try {
                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        UpdateProfileModel updateProfileModel = new Gson().fromJson(responce, UpdateProfileModel.class);
                        new SharedPrefModule(getActivity()).setUserLoginResponse(new Gson().toJson(updateProfileModel.response.response));
                        Toast.makeText(getActivity(), getResources().getString(R.string.updated_successfully), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), jsonObject.getJSONObject("response").getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onFailure(String url, Throwable throwable) {
        hideProgressDialog(getActivity());
    }

}