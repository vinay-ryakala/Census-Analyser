package censusanalyser;

public class CensusAnalyserException extends Exception {

    enum ExceptionType {
        CENSUS_FILE_PROBLEM,UNABLE_TO_PARSE,ERROR_FROM_CSV,SOME_FILE_ISSUE ;
    }

    ExceptionType type;

    public CensusAnalyserException(String message, ExceptionType type) {
        super(message);
        this.type = type;
    }
    /*
    public CensusAnalyserException(String message, ExceptionType type, Throwable cause) {
        super(message, cause);
        this.type = type;
    }*/
}
