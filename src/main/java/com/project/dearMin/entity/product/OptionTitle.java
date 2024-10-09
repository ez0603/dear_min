package com.project.dearMin.entity.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionTitle {
    private int optionTitleId;
    private int productId;
    private String titleName;
    private List<OptionName> optionNames;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}
