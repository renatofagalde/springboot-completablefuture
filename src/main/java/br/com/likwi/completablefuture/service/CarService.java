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

    @Async
    public CompletableFuture<List<Car>> getJoins() {

//        CompletableFuture<List<Car>> completableFutureCars = new CompletableFuture<>();
//        Future<List<Car>> futureCar1 = getAllCars().thenCompose(x -> CompletableFuture.allOf(x).join());
//        Future<List<Car>> futureCar2 = getAllCars();
//        Future<List<Car>> futureCar3 = getAllCars();

//        CompletableFuture<List<Car>> cars1 = getAllCars();
//        CompletableFuture<List<Car>> cars2 = getAllCars();
//        CompletableFuture<List<Car>> cars3 = getAllCars();
//        CompletableFuture.allOf(cars1, cars2, cars3).join();


//        final CompletableFuture<List<Car>> join = getAllCars().thenComposeAsync(this::getAllCars).join();

        final CompletableFuture<CompletableFuture<List<Car>>> cars1 = CompletableFuture.supplyAsync(() -> getAllCars());
        final CompletableFuture<CompletableFuture<List<Car>>> cars2 = CompletableFuture.supplyAsync(() -> getAllCars());
        final CompletableFuture<CompletableFuture<List<Car>>> cars3 = CompletableFuture.supplyAsync(() -> getAllCars());


/*
        final CompletableFuture<Bill> billFuture = CompletableFuture.supplyAsync(() -> billRepository.findOne(billId));
        final CompletableFuture<BillSummaryDTO> summaryDTOCompletableFuture = CompletableFuture.supplyAsync(() -> billItemRepository.summarizeByBillId(billId));
        final CompletableFuture<List<NumberUseDTO>> most10numbersUseFuture = CompletableFuture.supplyAsync(() -> billItemRepository.numberGreaterUse(billId, new PageRequest(0, 10)));

        try {
           final Bill bill = billFuture.get();
           final BillSummaryDTO summary = summaryDTOCompletableFuture.get();
           final List<NumberUseDTO> most10numbersUse = most10numbersUseFuture.get();

           return new BillResumeDTO(bill.getIdentifier(), bill.getCustomer().getName(), summary, most10numbersUse);
        } catch (InterruptedException | ExecutionException e) {
            log.error("findResume process error" + e);
            throw new RuntimeException();
        }
 */


        return  null;

    }
}
