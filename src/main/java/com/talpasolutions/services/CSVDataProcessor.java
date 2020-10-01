package com.talpasolutions.services;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.talpasolutions.model.Location;
import com.talpasolutions.model.LocationType;
import com.talpasolutions.model.RouteInfo;
import com.talpasolutions.model.RouteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CSVDataProcessor implements DataProcessor {

    private static final Character DATA_SEPARATOR = '|';

    @Value("${talpa.input}")
    private String inputFile;

    @Value("${talpa.output}")
    private String outputFile;

    private List<RouteInfo> preparedLocations;

    @PostConstruct
    public void init() {
        preparedLocations = uploadLocations(inputFile);
    }

    private List<RouteInfo> uploadLocations(String url) {
        try {
            log.info(String.format("Checking URL %s", url));
            List<RouteInfo> result = new ArrayList<>();
            URL fileUrl = this.getClass().getClassLoader().getResource(url);
            CSVParser parser = new CSVParserBuilder().withSeparator(DATA_SEPARATOR).build();
            CSVReader reader;
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(fileUrl).openStream()))) {
                reader = new CSVReaderBuilder(in).withCSVParser(parser).build();
                String[] line;
                while ((line = reader.readNext()) != null) {
                    RouteInfo current = Location.builder()
                            .name(line[0].trim())
                            .address(line[1].trim())
                            .latitude(Double.parseDouble(line[2].trim()))
                            .longitude(Double.parseDouble(line[3].trim()))
                            .type(LocationType.valueOf(line[4].trim()))
                            .build();
                    log.info(String.format("Added to analyze %s", current));
                    result.add(current);
                }
                return result;
            }
        } catch (IOException | CsvValidationException e) {
            log.error(String.format("Cannot read provided url %s", url));
            throw new IllegalArgumentException(e);
        }
    }

    public List<RouteInfo> readDepots() {
        return filterLocations.apply(preparedLocations, LocationType.DEPOT);
    }

    public List<RouteInfo> readStores() {
        return filterLocations.apply(preparedLocations, LocationType.STORE);
    }

    public List<RouteInfo> readCustomers() {
        return filterLocations.apply(preparedLocations, LocationType.CUSTOMER);
    }

    @Override
    public void saveRoutes(List<RouteResult> results) {
        try (
                Writer writer = Files.newBufferedWriter(Paths.get(outputFile));
                CSVWriter csvWriter = new CSVWriter(writer,
                        DATA_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END)
        ) {
            String[] headerRecord = {"Customer", "Store", "Depot", "Distance", "Duration"};
            csvWriter.writeNext(headerRecord);
            results.forEach(r -> csvWriter.writeNext(new String[]{
                    r.getCustomer().getName() + "{" + r.getCustomer().getAddress() + "}",
                    r.getStore().getName() + "{" + r.getStore().getAddress() + "}",
                    r.getDepot().getName() + "{" + r.getDepot().getAddress() + "}",
                    String.valueOf(r.getDistance()),
                    r.getMinSec()
            }));
        } catch (IOException e) {
            log.error(String.format("Cannot save results in the file %s", results));
            throw new IllegalStateException(e);
        }
    }

    private final BiFunction<List<RouteInfo>, LocationType, List<RouteInfo>> filterLocations = (locations, type) -> {
        List<RouteInfo> result = locations.stream().filter(location -> location.getType().equals(type))
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            throw new IllegalStateException(String.format("Cannot find %s locations.", type.name()));
        }
        return result;
    };

}
