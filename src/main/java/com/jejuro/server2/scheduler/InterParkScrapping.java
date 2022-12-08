package com.jejuro.server2.scheduler;

import com.jejuro.server2.entity.*;
import com.jejuro.server2.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterParkScrapping {

    private final FlightRepository flightRepository;
    private int DATE_RANGE = 2;

    @Scheduled(cron = "10 48 * * * *")
    public void crawlData() {
        LocalDate now = LocalDate.now();
        ArrayList<FlightInfo> flightInfos = new ArrayList<>();

        for (int day=1; day < DATE_RANGE; day++) {
            LocalDate localDate = now.plusDays(day);
            String date = localDate.toString().replaceAll("-","");

            // connection check
            Connection con = getConnection(date);

            // connection 으로 항공기 데이터 받아오기
            JSONArray data = getFlightData(con);

            //
            FlightInfo flightInfo = getFlightInfo(data);

            flightInfos.add(flightInfo);
        }
        String collectedDate = now.toString().replaceAll("-", "");

        Flight flight = Flight.builder()
                .date(collectedDate)
                .info(flightInfos)
                .build();

        flightRepository.save(flight);
    }

    /**
     * @param date api 에서 수집할 날짜
     * @return con Connection
     */
    private Connection getConnection(String date) {
        Connection con = Jsoup.connect(getUrl(date))
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36");

        return con;
    }

    /**
     * @param date api 에서 수집할 날짜
     * @return url date 를 적용한 api url
     */
    private String getUrl(String date) {
        String url = "https://sky.interpark.com/tourair/v1/schedule/domestic?dep=GMP&arr=CJU&depDate="
                +date+"&dep2=CJU&arr2=GMP&depDate2="+date+
                "&adt=1&inf=0&format=json&siteCode=WEBSTD&tripDivi=2";

        return url;
    }

    /**
     * @param con Connection 세션
     * @return availFareSet 항공기 정보 JSONArray 형태로 반환
     */
    public JSONArray getFlightData(Connection con) {
        JSONArray availFareSet = new JSONArray();
        try {
            String bodyText = con.get().body().text();
            JSONObject json = new JSONObject(bodyText);
            JSONObject body = json.getJSONObject("body");

            JSONObject jsonObjectFare = body.getJSONObject(InterParkKeyType.REPLY_AVAIL_FARE.getKey());
            JSONObject jsonObjectFareRT = body.getJSONObject(InterParkKeyType.REPLY_AVAIL_FARE_RT.getKey());

            JSONArray availFareSet1 = (JSONArray) jsonObjectFare.get("availFareSet");
            JSONArray availFareSet2 = (JSONArray) jsonObjectFareRT.get("availFareSet");

            for (Object o : availFareSet1) {
                availFareSet.put(o);
            }

            for (Object o : availFareSet2) {
                availFareSet.put(o);
            }

        } catch (IOException e) {
            log.info("parse error = {}", e);
        }
        return availFareSet;
    }

    public FlightInfo getFlightInfo(JSONArray availFareSet) {

        ArrayList<FlightDetail> jejuFlightDetails = new ArrayList<>();
        ArrayList<FlightDetail> gimpoFlightDetails = new ArrayList<>();

        String depDate = null;

        for (int i=0; i<availFareSet.length(); i++) {
            JSONObject availFareSetJson = availFareSet.getJSONObject(i);
            JSONObject segFare = availFareSetJson.getJSONObject("segFare");

            /**
             * 항공편 기본 정보
             */
            String depCity = (String)segFare.get("depCity"); // 출발지 코드
            String arrCity = (String)segFare.get("arrCity"); // 도착지 코드
            String carCode = (String)segFare.get("carCode"); // 항공사 코드
            String mainFlt = (String)segFare.get("mainFlt"); // 비행기 번호
            depDate = (String)segFare.get("depDate"); // 출발일자
            String depTime = (String)segFare.get("depTime"); // 출발시간
            String arrTime = (String)segFare.get("arrTime"); // 도착시간

            /**
             * 요금 정보 계산
             */
            int fuelChg = Integer.parseInt((String)segFare.get("fuelChg")); // 연료요금 oilTax
            int airTax = Integer.parseInt((String)segFare.get("airTax")); // 항공세
            int tasf = Integer.parseInt((String)segFare.get("tasf")); // 발권 수수료 commission

            JSONArray classDetail = (JSONArray) segFare.get("classDetail");
            ArrayList<PriceDetail> priceDetails = new ArrayList<>();

            /**
             * 좌석 정보에 따른 가격 priceDetail 객체로 만든 후 priceDetails에 저장
             */
            for (int j=0; j<classDetail.length(); j++) {
                JSONObject classDetailJson = classDetail.getJSONObject(j);
                String classDesc = (String)classDetailJson.get("classDesc");
                int fare = Integer.parseInt((String)classDetailJson.get("fare"));
                int fee = fuelChg + airTax + fare + tasf;

                if (classDesc.equals("일반석")) {
                    priceDetails.add(new PriceDetail(FlightFeeType.ECONOMY, fee));
                } else if (classDesc.equals("비즈니스석")) {
                    priceDetails.add(new PriceDetail(FlightFeeType.BUSINESS, fee));
                } else if (classDesc.equals("할인석")) {
                    priceDetails.add(new PriceDetail(FlightFeeType.DISCOUNT, fee));
                }
            }

            // flight detail
            FlightDetail flightDetail = FlightDetail.builder()
                    .airCode(carCode+mainFlt)
                    .depTime(depTime)
                    .arrTime(arrTime)
                    .priceDetails(priceDetails)
                    .build();

            // 도착지가 제주면 jejuFlightDetails 에 저장, 김포면 gimpoFlightDetails 에 저장
            if (arrCity.equals("CJU")) {
                jejuFlightDetails.add(flightDetail);
            } else {
                gimpoFlightDetails.add(flightDetail);
            }
        }

        JejuDest jejuDest = JejuDest.builder()
                .flightDetail(jejuFlightDetails)
                .build();
        GimpoDest gimpoDest = GimpoDest.builder()
                .flightDetail(gimpoFlightDetails)
                .build();

        FlightInfo flightInfo = FlightInfo.builder()
                .depDate(depDate)
                .jejuDest(jejuDest)
                .gimpoDest(gimpoDest)
                .build();

        return flightInfo;
    }
}
