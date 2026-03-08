package com.taxworkbench.api.application.workitem;

import com.taxworkbench.api.domain.workitem.WorkItemQuery;
import com.taxworkbench.api.infrastructure.streaming.CsvExporter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;

@Service
@RequiredArgsConstructor
public class ExportWorkItemUseCase {

    private final CsvExporter csvExporter;

    public void execute(WorkItemQuery query, OutputStream outputStream) throws IOException {
        csvExporter.export(query, outputStream);
    }
}