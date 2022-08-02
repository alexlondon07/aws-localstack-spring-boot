package com.aws.springbootlocalstackaws.repository;

import java.util.List;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.aws.springbootlocalstackaws.model.Car;

@EnableScan
public interface CarRepository extends CrudRepository<Car, String> {

    List<Car> findByModel(String model);

    List<Car> findByColor(String color);
}
