package com.valle.deliveryboyfoodieapp.models;

public class OrderUpdateModel {
    /*{
        "status": "success",
            "response": {
        "msg": "Actualizada",
                "orders_Info": {
            "Id": "93",
                    "Customer_Id": {
                "User_Id": "95",
                        "Full_Name": "customer ho me",
                        "Restaurant_Name": "",
                        "Email": "customerhome@gmail.com",
                        "Mobile": "9988998899",
                        "City": "",
                        "Address": "",
                        "Website": null,
                        "Latitude": "",
                        "Longitude": "",
                        "Role": "Customer",
                        "Login_Type": "Default",
                        "Set_Your_Presence": "",
                        "Profile_Image": "IMG_20191227_175905_8131731305066107486.jpg",
                        "Cover_Image": null,
                        "Overall_Rating": "0.0",
                        "Discount_Code": null,
                        "Discount_Percentage": null,
                        "Refer_Code": "VF5667",
                        "Validation_Code": "3642",
                        "Is_Active": "Active",
                        "Device_Token": "",
                        "createdDtm": "2019-12-20 15:13:10"
            },
            "Delivery_Boy_Id": {
                "User_Id": "94",
                        "Full_Name": "Developer Team ",
                        "Restaurant_Name": "Developer",
                        "Email": "testing@gmail.com",
                        "Mobile": "9876543222",
                        "City": "Panchkula",
                        "Address": "Sector 5",
                        "Website": null,
                        "Latitude": "30.727921",
                        "Longitude": "76.839661",
                        "Role": "Customer",
                        "Login_Type": "Default",
                        "Set_Your_Presence": "",
                        "Profile_Image": null,
                        "Cover_Image": null,
                        "Overall_Rating": "0.0",
                        "Discount_Code": null,
                        "Discount_Percentage": null,
                        "Refer_Code": "VF4316",
                        "Validation_Code": "",
                        "Is_Active": "Active",
                        "Device_Token": "",
                        "createdDtm": "2019-12-18 10:55:23"
            },
            "Rest_Id": {
                "User_Id": "105",
                        "Full_Name": "Gopal's",
                        "Restaurant_Name": "Gopal's",
                        "Email": "gopal@gmail.com",
                        "Mobile": "8521456155",
                        "City": "Chandigarh",
                        "Address": "Sector 5",
                        "Website": null,
                        "Latitude": "30.7279221",
                        "Longitude": "76.839661",
                        "Role": "Restaurant",
                        "Login_Type": "Default",
                        "Set_Your_Presence": "ON",
                        "Profile_Image": "profile1.jpg",
                        "Cover_Image": "cover2.jpg",
                        "Overall_Rating": "1.0",
                        "Discount_Code": "GOPAL30",
                        "Discount_Percentage": "30",
                        "Refer_Code": "VF6720",
                        "Validation_Code": "",
                        "Is_Active": "Active",
                        "Device_Token": "Device_Token",
                        "createdDtm": "2019-12-27 13:29:39"
            },
            "Order_Number": "ON951051",
                    "Order_Status": "Order Confirmed",
                    "DeliveryBoy_Status": "Accepted",
                    "Delivery_Time": null,
                    "Grand_Total": "220000",
                    "Payment_Type": "Cash",
                    "Payment_Status": "UnPaid",
                    "Payment_History": null,
                    "Ordered_Items": {
                "Items": [
                {
                    "Id": "18",
                        "Quantity": "1",
                        "Item_Name": "Mutton Rogan Josh",
                        "Item_Category": "DESAYUNO",
                        "Item_Price": "120000",
                        "Item_Image": "chicken5.jpg"
                }
                ],
                "Delivery_Address": {
                    "Map_Address": "House no 123",
                            "Address": "Sector 15, Chandigarh",
                            "Delivery_Lat": "30.7565",
                            "Delivery_Long": "76.4547"
                },
                "Tax": "50000",
                        "Delivery_Charges": "50000"
            },
            "Wishlist": "No",
                    "Custom_Note": "",
                    "Is_Rating": "0",
                    "createdDtm": "2020-01-02 22:54:31"
        }
    }
    }*/

    public responseData response;

    public class responseData {
        public OrderHistoryModel.responseData.orders_InfoData orders_Info;
    }
}
