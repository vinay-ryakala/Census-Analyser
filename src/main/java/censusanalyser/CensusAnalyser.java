package censusanalyser;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

public class CensusAnalyser
{
    List<IndiaCensusCSV> csvFileList = null;
    public int loadIndiaCensusData(String csvFilePath) throws CensusAnalyserException
    {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));) {
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
            List<IndiaStateCodeCSV> csvFileList =  csvBuilder.
                    getCSVFileList(reader,IndiaStateCodeCSV.class);
            return csvFileList.size();
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

        Comparator<IndiaCensusCSV> censusComparator = Comparator.comparing(census -> census.state);
        this.sort(csvFileList, censusComparator);
        String toJson = new Gson().toJson(csvFileList);
        return toJson;
    }

    private void sort(List<IndiaCensusCSV> csvFileList, Comparator<IndiaCensusCSV> censusComparator) {
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
}