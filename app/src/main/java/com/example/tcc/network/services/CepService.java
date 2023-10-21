package com.example.tcc.network.services;

import com.example.tcc.network.entities.CepApi;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CepService {


    @GET("api/cep/v2/{cep}")
    Call<CepApi> getCidadeEstadoByCEP(@Path("cep") Integer cep);

}
