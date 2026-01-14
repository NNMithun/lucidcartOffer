package com.testng;

import com.springboot.controller.ApplyOfferRequest;
import com.springboot.controller.ApplyOfferResponse;
import com.springboot.controller.OfferRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;

public class CartOfferApplicationTests {
     private String baseUri="http://localhost:9001";
     private String ADD_OFFER_TO_RESTAURANT_API="/api/v1/offer";
     private String APPLY_OFFER_API="/api/v1/cart/apply_offer";


    @BeforeClass
    public void addOffersToRestaurant(){
        RestAssured.baseURI =baseUri ;
        OfferRequest offerRequest=buildOfferRequest("p1",10,"FLATX",11);
        Response response=apiExecutor(offerRequest,ADD_OFFER_TO_RESTAURANT_API);
        Assert.assertEquals(response.getStatusCode(), 200);

        offerRequest=buildOfferRequest("p2",10,"FLAT%",11);
        response=apiExecutor(offerRequest,ADD_OFFER_TO_RESTAURANT_API);
        Assert.assertEquals(response.getStatusCode(), 200);

        offerRequest=buildOfferRequest("p3",90,"FLAT%",11);
        response=apiExecutor(offerRequest,ADD_OFFER_TO_RESTAURANT_API);
        Assert.assertEquals(response.getStatusCode(), 200);

        offerRequest=buildOfferRequest("p1",0,"FLATX",12);
        response=apiExecutor(offerRequest,ADD_OFFER_TO_RESTAURANT_API);
        Assert.assertEquals(response.getStatusCode(), 200);

        offerRequest=buildOfferRequest("p2",100,"FLAT%",12);
        response=apiExecutor(offerRequest,ADD_OFFER_TO_RESTAURANT_API);
        Assert.assertEquals(response.getStatusCode(), 200);

        offerRequest=buildOfferRequest("p3",100,"FLAT%",12);
        response=apiExecutor(offerRequest,ADD_OFFER_TO_RESTAURANT_API);
        Assert.assertEquals(response.getStatusCode(), 200);

    }
    private OfferRequest buildOfferRequest(String customerSegment,int offerValue, String offerType,int restaurantId){
        OfferRequest offerRequest=new OfferRequest();
        List<String> customerSegments=new ArrayList<>();
        customerSegments.add(customerSegment);
        offerRequest.setOffer_value(offerValue);
        offerRequest.setOffer_type(offerType);
        offerRequest.setCustomer_segment(customerSegments);
        offerRequest.setRestaurant_id(restaurantId);

        return offerRequest;
    }

    private ApplyOfferRequest applyOfferRequest( int restaurantId, int userId,int cartValue){
        ApplyOfferRequest applyOfferRequest= new ApplyOfferRequest();
        applyOfferRequest.setCart_value(cartValue);
        applyOfferRequest.setRestaurant_id(restaurantId);
        applyOfferRequest.setUser_id(userId);

        return applyOfferRequest;

    }

    private Response apiExecutor(Object requestBody,String apiUrl ){
        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().log().all()
                .post(apiUrl)
                .then()
                .log().all()
                .extract().response();
    }



    @Test(dataProvider ="offerData" )
    public void verifyCartValue(ApplyOfferRequest applyOfferRequest, int expectedCartValue){
       Response response=apiExecutor(applyOfferRequest,APPLY_OFFER_API);
        int actualCartValue=response.jsonPath().getInt("cart_value");
       Assert.assertEquals(actualCartValue,expectedCartValue);

    }



    @DataProvider(name = "offerData")
    public Object[][] offerDataProvider() {

        return new Object[][]{

                {applyOfferRequest(11,1,200),190},
                {applyOfferRequest(11,2,200),180},
                {applyOfferRequest(11,3,200),20},
                {applyOfferRequest(12,1,200),200},
                {applyOfferRequest(12,2,50),0},
                {applyOfferRequest(12,3,1000),0},


        };
    }
}
