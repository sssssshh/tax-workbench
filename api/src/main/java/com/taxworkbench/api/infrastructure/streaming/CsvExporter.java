package com.taxworkbench.api.infrastructure.streaming;

import com.taxworkbench.api.domain.workitem.WorkItem;
import com.taxworkbench.api.domain.workitem.WorkItemQuery;
import com.taxworkbench.api.domain.workitem.WorkItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CsvExporter {

    private final WorkItemRepository workItemRepository;

    @Transactional(readOnly = true)
    public void export(WorkItemQuery query, OutputStream outputStream) throws IOException {
        List<WorkItem> items = workItemRepository.findAllForExport(query);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {

            writer.write('\uFEFF');
            writer.write("ID,업체명,사업자번호,업무유형,상태,담당자,마감일,메모,생성일");
            writer.newLine();

            for (WorkItem item : items) {
                writer.write(toCsvRow(item));
                writer.newLine();
            }

            writer.flush();
        }
    }

    private String toCsvRow(WorkItem item) {
        return String.join(",",
                safe(String.valueOf(item.getId())),
                safe(item.getClientName()),
                safe(item.getBizNo()),
                safe(item.getType() == null ? null : item.getType().name()),
                safe(item.getStatus() == null ? null : item.getStatus().name()),
                safe(item.getAssignee()),
                safe(String.valueOf(item.getDueDate())),
                safe(item.getMemo()),
                safe(String.valueOf(item.getCreatedAt()))
        );
    }

    private String safe(String value) {
        if (value == null || value.equals("null")) return "";
        if (value.contains(",") || value.contains("\n") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}