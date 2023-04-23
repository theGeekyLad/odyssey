package com.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetStopView {
    List<StopInfo> stopInfos;
    int itemPerStop;
    double radius;
    List<String> types;
}
