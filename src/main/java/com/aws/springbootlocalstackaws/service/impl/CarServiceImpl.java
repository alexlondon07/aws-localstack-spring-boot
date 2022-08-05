package com.aws.springbootlocalstackaws.service.impl;

import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.aws.springbootlocalstackaws.model.Car;
import com.aws.springbootlocalstackaws.repository.CarRepository;
import com.aws.springbootlocalstackaws.service.CarService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@AllArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Override
    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    @Override
    public List<Car> findAll() {
        return StreamSupport
                .stream(carRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> findByColor(String color) {
        return carRepository.findByColor(color);
    }

    @Override
    public List<Car> findByModel(String model) {
        return carRepository.findByModel(model);
    }

}
