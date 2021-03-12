package br.com.likwi.completablefuture.service;

import br.com.likwi.completablefuture.dao.entity.Car;
import br.com.likwi.completablefuture.dao.repository.CarRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class CarService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarService.class);

    @Autowired
    private CarRepository carRepository;

    @Async
    public CompletableFuture<List<Car>> saveCars(final InputStream inputStream) throws Exception {
        final long start = System.currentTimeMillis();

        List<Car> cars = parseCSVFile(inputStream);
        LOGGER.info("Saving a list of cars of size {} records, car {}", cars.size(), cars.get(0).getManufacturer());
        Thread.sleep(15000);

        cars = carRepository.saveAll(cars);

        LOGGER.info("Elapsed time: {} para file {}", (System.currentTimeMillis() - start), cars.get(0).getManufacturer());
        return CompletableFuture.completedFuture(cars);
    }

    private List<Car> parseCSVFile(final InputStream inputStream) throws Exception {

        final List<Car> cars = new ArrayList<>();

        try {
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    final String[] data = line.split(";");
                    final Car car = new Car();
                    car.setManufacturer(data[0]);
                    car.setModel(data[1]);
                    car.setType(data[2]);
                    cars.add(car);
                }
                return cars;
            }
        } catch (final IOException e) {
            LOGGER.error("Failed to parse CSV file {}", e);
            throw new Exception("Failed to parse CSV file {}", e);
        }
    }

    @Async
    public CompletableFuture<List<Car>> getAllCars() {

        LOGGER.info("Request to get a list of cars");

        final List<Car> cars = carRepository.findAll();
        return CompletableFuture.completedFuture(cars);
    }

    public List<Car> getAllCarsRoot() {

        LOGGER.info("Request to get a list of cars");

        return carRepository.findAll();
    }


    public List<Car> getCarCompletableFuture() {

        List<Car> cr1 = null;

        final CompletableFuture<List<Car>> cars1 = CompletableFuture.supplyAsync(() -> getAllCarsRoot());
        final CompletableFuture<List<Car>> cars2 = CompletableFuture.supplyAsync(() -> getAllCarsRoot());
        final CompletableFuture<List<Car>> cars3 = CompletableFuture.supplyAsync(() -> getAllCarsRoot());

        try {

            cr1 = cars1.get();
            cr1.addAll(cars2.get());
            cr1.addAll(cars3.get());

        } catch (InterruptedException | ExecutionException e) {
            LOGGER.info("error {}", e.getLocalizedMessage());
            throw new RuntimeException();
        }

        return cr1;


    }
}
