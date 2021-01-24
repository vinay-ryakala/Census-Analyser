package censusanalyser;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

public class CensusAnalyser
{
    List<IndiaCensusCSV> csvFileList = null;
    List<IndiaStateCodeCSV> stateCSVList = null;
    public int loadIndiaCensusData(String csvFilePath) throws CensusAnalyserException
    {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath))) {
            ICsvBuilder csvBuilder = CsvBuilderFactory.createCsvBuilder();
           List<IndiaCensusCSV> csvFileList = csvBuilder.
                    getCSVFileList(reader,IndiaCensusCSV.class);
            return csvFileList.size();
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (RuntimeException | CSVBuilderException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.ERROR_FROM_CSV);
        }
    }

    public int loadIndiaStateCode(String csvFilePath) throws CensusAnalyserException
    {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));) {
            ICsvBuilder csvBuilder = CsvBuilderFactory.createCsvBuilder();
            List<IndiaStateCodeCSV> stateCSVList =  csvBuilder.
                    getCSVFileList(reader,IndiaStateCodeCSV.class);
            return stateCSVList.size();
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (RuntimeException | CSVBuilderException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.ERROR_FROM_CSV);
        }
    }

    private <E> int getCount(Iterator<E> iterator){
        Iterable<E> csvIterable = () -> iterator;
        int numOfEntries = (int) StreamSupport.stream(csvIterable.spliterator(), false)
                .count();
        return numOfEntries;
    }
    public String getStateWiseSortedCensusData(String csvFilePath) throws CensusAnalyserException {
        loadIndiaCensusData(csvFilePath);
        if (csvFileList == null || csvFileList.size() == 0) {
            throw new CensusAnalyserException("NO_CENSUS_DATA", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<IndiaCensusCSV> censusComparator = Comparator.comparing(census->census.state);
        this.sortCensusbyState(censusComparator);
        String sortedStateCensusJson = new Gson().toJson(csvFileList);
        return sortedStateCensusJson;
    }
    public String getStateCodeWiseSortedCensusData(String csvFilePath) throws CensusAnalyserException {
        if(stateCSVList == null || stateCSVList.size() == 0) {
            throw new CensusAnalyserException("File is empty", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<IndiaStateCodeCSV> censusComparator = Comparator.comparing(census->census.stateCode);
        this.sortStateCodeCSV(censusComparator);
        String sortedStateCodeCensusJson = new Gson().toJson(stateCSVList);
        return sortedStateCodeCensusJson;
    }
    public String getPopulationWiseSortedCensusData() throws CensusAnalyserException{
        if(csvFileList == null || csvFileList.size() == 0){
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<IndiaCensusCSV> censusComparator = Comparator.comparing(census->census.population);
        this.sortCensusbyState(censusComparator);
        String sortedPopulationCensusJson = new Gson().toJson(stateCSVList);
        return sortedPopulationCensusJson;
    }
    public String getPopulationdensitySortedCensusData() throws CensusAnalyserException{
        if(csvFileList == null || csvFileList.size() == 0){
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<IndiaCensusCSV> censusComparator = Comparator.comparing(census->census.densityPerSqKm);
        this.sortCensusbyState(censusComparator);
        String sortedPopulationDensityCensusJson = new Gson().toJson(csvFileList);
        return sortedPopulationDensityCensusJson;
    }
    public String getAreaSortedCensusData() throws CensusAnalyserException{
        if(csvFileList == null || csvFileList.size() == 0){
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<IndiaCensusCSV> censusComparator = Comparator.comparing(census->census.areaInSqKm);
        this.sortCensusbyState(censusComparator);
        String sortedAreaCensusJson = new Gson().toJson(csvFileList);
        return sortedAreaCensusJson;
    }

    private void sortCensusbyState(Comparator<IndiaCensusCSV> censusComparator) {
        for (int i = 0; i < csvFileList.size(); i++) {
            for (int j = 0; j < csvFileList.size() - i - 1; j++) {
                IndiaCensusCSV census1 = csvFileList.get(j);
                IndiaCensusCSV census2 = csvFileList.get(j + 1);
                if (censusComparator.compare(census1, census2) > 0) {
                    csvFileList.set(j, census2);
                    csvFileList.set(j + 1, census1);
                }
            }
        }
    }
    private void sortStateCodeCSV(Comparator<IndiaStateCodeCSV> indiaStateCodeComparator){
        for(int i=0;i<stateCSVList.size()-1;i++){
            for(int j=0; j<stateCSVList.size()-1;j++){
                IndiaStateCodeCSV census1 = stateCSVList.get(j);
                IndiaStateCodeCSV census2 = stateCSVList.get(j+1);
                if(indiaStateCodeComparator.compare(census1,census2)>0){
                    stateCSVList.set(j, census2);
                    stateCSVList.set(j+1, census1);
                }
            }
        }
    }
}
