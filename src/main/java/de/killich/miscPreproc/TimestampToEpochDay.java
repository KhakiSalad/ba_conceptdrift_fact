package de.killich.miscPreproc;

import stream.AbstractProcessor;
import stream.Data;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Processor that converts a date represented by a String to the corresponding EpochDay. For conversion <code>java.time.LocalDate</code>
 * and <code>java.time.format.DateTimeFormatter</code> are used.
 *
 * @see java.time.format.DateTimeFormatter
 * @see java.time.LocalDate
 * @see stream.AbstractProcessor
 * @author nilskillich
 */
public class TimestampToEpochDay extends AbstractProcessor{
    private String indexKey = "date";
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private LocalDate firstDay ;


    @Override
    public Data process(Data item){
        LocalDate date = LocalDate.parse((String)item.get(indexKey), dateFormatter);
        item.put(indexKey,date.toEpochDay());
        return item;
    }
}
