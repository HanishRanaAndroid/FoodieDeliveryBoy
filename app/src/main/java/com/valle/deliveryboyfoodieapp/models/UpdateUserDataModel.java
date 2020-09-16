package com.valle.deliveryboyfoodieapp.models;

public class UpdateUserDataModel {

/*    {
        "status": "success",
            "response": {
        "msg": "Encontró",
                "orders_Info": {
            "User_Id": "89",
                    "Full_Name": "Delivery Boy",
                    "Restaurant_Name": "",
                    "Email": "DeliveryBoy@gmail.com",
                    "Mobile": "3211233214",
                    "Password": "$2y$10$8rsY4fWxDbR0jSP2HiPEq.uBrCaxU07rLhS1dfFVDFYXgizoYSYvq",
                    "City": "Chandigarh",
                    "Address": "Sector 5",
                    "Website": null,
                    "Latitude": "30.722041",
                    "Longitude": "76.851761",
                    "Role": "Delivery_Boy",
                    "Login_Type": "Default",
                    "Set_Your_Presence": "OFF",
                    "Profile_Image": "IMG_20191214_162009_1919978022791123804.jpg",
                    "Cover_Image": "",
                    "Overall_Rating": "5",
                    "Discount_Code": "",
                    "Discount_Percentage": null,
                    "Refer_Code": "BL7181",
                    "Validation_Code": "",
                    "Is_Active": "Active",
                    "Device_Token": "",
                    "createdDtm": "2019-12-14 16:02:13"
        }
    }
    }*/


    public Response response;
    public class Response{
        public LoginModel.responseData.UserInfoData orders_Info;

    }


}
