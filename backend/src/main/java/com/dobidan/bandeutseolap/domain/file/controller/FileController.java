package com.dobidan.bandeutseolap.domain.file.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5173") // 필요한 도메인만!
@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class FileController {
}
