package com.weatherly.weatherly.modules.mainscreen.core.interactor;

import com.weatherly.weatherly.application.builder.MyApplicationModule;
import com.weatherly.weatherly.modules.common.WeatherRequest;
import com.weatherly.weatherly.modules.common.entities.WeatherGeneralModel;
import com.weatherly.weatherly.modules.mainscreen.core.entities.WeatherDataModel;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DefaultMainScreenInteractor implements MainScreenInteractor {
    private Retrofit retrofit;
    private WeatherRequest request;
    private MainScreenInteractorOutput callbacks;

    public DefaultMainScreenInteractor(Retrofit retrofit) {
        this.retrofit = retrofit;
        request = retrofit.create(WeatherRequest.class);
    }

    @Override
    public void getWeatherByCityName() {
        request.getByCityName("Dublin", MyApplicationModule.UNITS, MyApplicationModule.USER_KEY).enqueue(new Callback<WeatherGeneralModel>() {
            @Override
            public void onResponse(Call<WeatherGeneralModel> call, Response<WeatherGeneralModel> response) {
                if (response.body() != null) {
                    Calendar rightNow = Calendar.getInstance();
                    int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
                    String hour = (currentHour > 4 && currentHour < 18) ? "day" : "night";

                    WeatherGeneralModel model = response.body();
                    WeatherDataModel dataModel = new WeatherDataModel(model.getName(),
                            parseTemperature(model.getMain().getTemp()),
                            model.getWeather().get(0).getIcon(),
                            hour,
                            model.getMain().getPressure() + " hpa",
                            model.getMain().getHumidity() + "%",
                            model.getWind().getSpeed() + " m/s",
                            model.getWeather().get(0).getDescription(),
                            model.getSys().getCountry(),
                            model.getClouds().getAll() + "%");

                    callbacks.onGetWeatherByCityNameSuccess(dataModel);
                } else {
                    callbacks.onGetWeatherByCityNameError("error");
                }
            }

            @Override
            public void onFailure(Call<WeatherGeneralModel> call, Throwable t) {
                callbacks.onGetWeatherByCityNameError(t.toString());
            }
        });

    }

    @Override
    public String parseTemperature(String temp) {
        double doubleTemp = Double.parseDouble(temp);
        return String.valueOf((int) doubleTemp);
    }

    @Override
    public void setCallbacks(MainScreenInteractorOutput callbacks) {
        this.callbacks = callbacks;
    }
}
