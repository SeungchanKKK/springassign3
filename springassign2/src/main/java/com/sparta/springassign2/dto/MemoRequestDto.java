package com.sparta.springassign2.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MemoRequestDto {
    private String username;
    private String contents;
    private String title;
}