package com.example.asm1.Api;

import com.example.asm1.Model.ProductModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl("http://192.168.55.115:8000/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @GET("listCar")
    Call<List<ProductModel>> getProduct();

    @POST("addCars")
    Call<ProductModel> addCar(@Body ProductModel productModel);

    @PUT("cars/{id}")
    Call<ProductModel> updateCar(@Path("id") String id, @Body ProductModel productModel);

    @DELETE("/cars/{id}")
    Call<Void> deleteCars(@Path("id") String id);

}
