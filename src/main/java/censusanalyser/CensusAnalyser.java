package censusanalyser;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.StreamSupport;

public class CensusAnalyser
{
    public int loadIndiaCensusData(String csvFilePath) throws CensusAnalyserException
    {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));) {
            ICsvBuilder csvBuilder = CsvBuilderFactory.createCsvBuilder();
            Iterator<IndiaCensusCSV> censusCSVIterator = csvBuilder.
                                getCSVFileIterator(reader,IndiaCensusCSV.class);
            return this.getCount(censusCSVIterator);
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (RuntimeException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.ERROR_FROM_CSV);
        }
    }

    public int loadIndiaStateCode(String csvFilePath) throws CensusAnalyserException
    {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));) {
            ICsvBuilder csvBuilder = CsvBuilderFactory.createCsvBuilder();
            Iterator<IndiaStateCodeCSV> censusCSVIterator =  csvBuilder.
                                getCSVFileIterator(reader,IndiaStateCodeCSV.class);
            return this.getCount(censusCSVIterator);
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (RuntimeException e) {
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
}
