package com.main.icrsbackend.dto.technicaltestdto;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitTestRequest {
    private Long userId;
    private String name;
    private String stream;
    private String skill;
    private String level;
    private Integer setNumber;
    private List<AnswerRequest> answers;
}