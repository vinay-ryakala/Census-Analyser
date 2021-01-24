package censusanalyser;

import java.io.Reader;
import java.util.Iterator;

public interface ICsvBuilder
{
   public <E> Iterator<E> getCSVFileIterator(Reader reader, Class csvClass) throws CSVBuilderException;
}