package com.aws.springbootlocalstackaws.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aws.springbootlocalstackaws.model.Car;
import com.aws.springbootlocalstackaws.service.CarService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping
    public ResponseEntity<Car> newCar(@Valid @RequestBody Car car) {
        return new ResponseEntity<>(carService.saveCar(car), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Car>> findCards(@Param("color") String color,
                                               @Param("model") String model) {
        if (Strings.isNotEmpty(color)) {
            return ResponseEntity.ok(carService.findByColor(color));
        } else if (Strings.isNotEmpty(model)) {
            return ResponseEntity.ok(carService.findByModel(model));
        }
        return ResponseEntity.ok(carService.findAll());
    }


}
