package com.aws.springbootlocalstackaws.service;

import java.util.List;

import com.aws.springbootlocalstackaws.model.Car;

public interface CarService {

    Car saveCar(Car car);

    List<Car> findAll();

    List<Car> findByColor(String color);

    List<Car> findByModel(String model);
}
