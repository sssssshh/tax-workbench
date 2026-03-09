package com.taxworkbench.api.infrastructure.streaming;

import com.taxworkbench.api.domain.workitem.WorkItemQuery;
import com.taxworkbench.api.infrastructure.persistence.workitem.WorkItemJpaRepository;
import com.taxworkbench.api.domain.workitem.WorkItemStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class CsvExporter {

    private final WorkItemJpaRepository workItemJpaRepository;

    @Transactional(readOnly = true)
    public void export(WorkItemQuery query, OutputStream outputStream) throws IOException {
        WorkItemStatus status = query.status();

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
             Stream<com.taxworkbench.api.infrastructure.persistence.workitem.WorkItemJpaEntity> stream =
                     workItemJpaRepository.streamForExport(
                             query.clientName(),
                             status,
                             query.assignee())) {

            // BOM - 엑셀 한글 깨짐 방지
            writer.write('\uFEFF');
            writer.write("ID,업체명,사업자번호,업무유형,상태,담당자,마감일,메모,생성일");
            writer.newLine();

            // 진짜 스트리밍 - 1000건씩 DB에서 가져와서 바로 씀
            stream.forEach(entity -> {
                try {
                    writer.write(toCsvRow(entity));
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException("CSV 스트리밍 중 오류 발생", e);
                }
            });

            writer.flush();
        }
    }

    private String toCsvRow(com.taxworkbench.api.infrastructure.persistence.workitem.WorkItemJpaEntity entity) {
        return String.join(",",
                safe(String.valueOf(entity.getId())),
                safe(entity.getClientName()),
                safe(entity.getBizNo()),
                safe(entity.getType().name()),
                safe(entity.getStatus().name()),
                safe(entity.getAssignee()),
                safe(String.valueOf(entity.getDueDate())),
                safe(entity.getMemo()),
                safe(String.valueOf(entity.getCreatedAt()))
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