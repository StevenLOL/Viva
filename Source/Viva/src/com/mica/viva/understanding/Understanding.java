/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mica.viva.understanding;

import java.util.ArrayList;

import com.mica.viva.entity.Function;
import com.mica.viva.entity.Parameter;

import constant.KeywordType;
import constant.StageType;



/**
 *
 * @author Truy Thu
 */
public class Understanding {

    private KeywordTokenizer kt = new KeywordTokenizer();
    private StageType stage;
    private DemoResult function;
    private Keyword keyword;
    private String messageContent = "";
    private DemoParameter tempParam = null;
    private final String WEATHER_MODULE_CODE = "WEATHER";
    private final String WEATHER_FUNCTION_CODE = "WEATHER";
    private final String WEATHER_TIME_PARAM_CODE = "Time";
    private final String WEATHER_PLACE_PARAM_CODE = "Place";

    public Understanding() {
        stage = StageType.StartStage;
    }

    public void newTransaction() {
        kt = new KeywordTokenizer();
        stage = StageType.StartStage;
        function = new DemoResult();
        messageContent = "";
        tempParam = null;
    }

    public Function understandCommand(String command) {
        kt.parserString(command);
        //System.out.println("Understanding...");
        
        keyword = kt.next();
        //System.out.println(stage);
        switch (stage) {
            case StartStage:
            case CompletedMessage:
            case WeatherHadPlace :
            case WeatherHadTime :
            case CompletedWeather :
            case HadContentNoContact :
            case HadContactNoContent :
                function = new DemoResult();
                messageContent = "";
                compileStartStage();
                break;
            case HadPlace :
            case HadTime :
            case Weather :
            case HadTimeAndPlace :
            	compileStartStage();
            	break;
        }
        
        ArrayList<Parameter> parameters_ = null;
        DemoParameter demo[] = function.getParameters();
        
        for (int i=0; i<function.i;i++) {
        	parameters_.add(new Parameter(demo[i].getParameter(), demo[i].getValue(), "",true));
        }
        Function function_ = new Function(function.getfunctionCode(), null, null, parameters_, null, null, null,null);
        
        return function_;
    }

    /**
     * @function 
     */
    public void compileStartStage() {
        //System.out.println("Compile StartStage");

        while (kt.hasNext()) {
            switch (keyword.type) {
                case ToMessage:
                    compileMessage();
                    break;
                case ToWeather :
                    compileWeather();
                    break;
                case Time :
                    compileHadTime();
                    break;
                case Place :
                    compileHadPlace();
                    break;
                case ToContact :
                    keyword = kt.next();
                    if (keyword.type == KeywordType.ContactName) {
                        tempParam = new DemoParameter("RECEIVER", keyword.keyword);
                    }
                    break;
                default : 
                    keyword = kt.next();
            }
        }
    }

    private void compileMessage() {
        //System.out.println("Compile Message");
        stage = StageType.Message;
        function = new DemoResult("SENDING_SMS", "SMS");
        keyword = kt.next();
        if (keyword.type == KeywordType.End) {
            return;
        }
        stage = StageType.Message;
        
        if (tempParam!=null) function.addParameter(tempParam);

        if (keyword.type == KeywordType.ToContact) {
            compileWaitContactNoContent();
        } else {
            compileWaitContentNoContact();
        }
    }

    private void compileWaitContentNoContact() {
        //System.out.println("Compile Wait Content No Contact");
        if (keyword.type == KeywordType.Tocontent) {
            keyword = kt.next();
        }
        while (true) {
            if (keyword.type == KeywordType.End) {
                compileHadContentNoContact();
                return;
            }
            if (keyword.type == KeywordType.ToContact) {
                compileWaitContactHadContent();
                return;
            }
            messageContent += keyword.keyword + " ";
            keyword = kt.next();
        }
    }

    private void compileWaitContactHadContent() {
        //System.out.println("Compile Wait Contact Had Content");
        String temp = messageContent;
        messageContent += " " + keyword.keyword;
        keyword = kt.next();

        if (keyword.type == KeywordType.ContactName) {
            messageContent = temp;
            function.addParameter(new DemoParameter("RECEIVER", keyword.keyword));
            compileCompletedMessage();
            return;
        } else {
            compileWaitContentNoContact();
        }
    }

    private void compileHadContentNoContact() {
        //System.out.println("Compile Had Content No Contact");
        stage = StageType.HadContentNoContact;
        
        if (keyword.type == KeywordType.End) { //System.out.println(keyword.type);
            function.addParameter(new DemoParameter("CONTENT", messageContent));
        } else {
            if (keyword.type == KeywordType.ToContact) {
                keyword = kt.next();
            }
            if (keyword.type == KeywordType.ContactName) {
                function.addParameter(new DemoParameter("RECEIVER", keyword.keyword));
                stage = StageType.StartStage;
            }
        }

    }

    private void compileWaitContactNoContent() {
        //System.out.println("Compile Wait Contact No Content");
        messageContent += keyword.keyword;
        keyword = kt.next();

        if (keyword.type == KeywordType.ContactName) {
            messageContent = "";
            function.addParameter(new DemoParameter("RECEIVER", keyword.keyword));
            compileHadContactNoContent();
            return;
        } else {
            compileWaitContentNoContact();
        }
    }

    private void compileHadContactNoContent() {
        //System.out.println("Compile Had Contact No Content");
        stage = StageType.HadContactNoContent;
        keyword = kt.next();
        switch (keyword.type) {
            case End:
                return;
            case Tocontent:
                keyword = kt.next();
            default:
                compileWaitContentHadContact();
        }
    }

    private void compileWaitContentHadContact() {
        //System.out.println("Compile Wait Content Had Contact");
        messageContent += keyword.keyword;
        while (true) {
            if (keyword.type == KeywordType.End) {
                compileCompletedMessage();
                break;
            }
            keyword = kt.next();
            messageContent += " " + keyword.keyword;
        }
    }

    private void compileCompletedMessage() {
        //System.out.println("Compile Completed Message");
        function.addParameter(new DemoParameter("CONTENT", messageContent));
        stage = StageType.CompletedMessage;
        
        while (kt.hasNext()) kt.next();
        return;
    }
    
    private void compileWeather() {
        //System.out.println("Compile Weather");
        stage = StageType.Weather;
        function.setFunction(WEATHER_FUNCTION_CODE, WEATHER_MODULE_CODE);
        keyword = kt.next();
        
        while (true) {
            switch (keyword.type) {
                case End :
                    return;
                case Place :
                	function.addParameter(new DemoParameter(WEATHER_PLACE_PARAM_CODE, keyword.keyword));
                    compileWeatherWithPlace();
                    break;
                case Time :
                	function.addParameter(new DemoParameter(WEATHER_TIME_PARAM_CODE, keyword.keyword));
                    compileWeatherWithTime();
                    break;
                default :
                    keyword = kt.next();
            }
        }
    }
    
    private void compileHadTime() {
        //System.out.println("Compile Had Time");
        stage = StageType.HadTime;
        function = new DemoResult();
        function.addParameter(new DemoParameter(WEATHER_TIME_PARAM_CODE, keyword.keyword));
        keyword = kt.next();
        
        while (true) {
            switch (keyword.type) {
                case End :
                    return;
                case ToWeather :
                    function.setFunction(WEATHER_FUNCTION_CODE, WEATHER_MODULE_CODE);
                    compileWeatherWithTime();
                    break;
                case Place :
                    function.addParameter(new DemoParameter(WEATHER_PLACE_PARAM_CODE, keyword.keyword));
                    compileHadTimeAndPlace();
                    break;
                default :
                    keyword = kt.next();
            }
        }
        
    }
    
    private void compileHadPlace() {
        //System.out.println("Compile Had Place");
        stage = StageType.HadPlace;
        function = new DemoResult();
        function.addParameter(new DemoParameter(WEATHER_PLACE_PARAM_CODE, keyword.keyword));
        keyword = kt.next();
        
        while (true) {
            switch (keyword.type) {
                case End :
                    return;
                case ToWeather :
                	function.setFunction(WEATHER_FUNCTION_CODE, WEATHER_MODULE_CODE);
                    compileWeatherWithPlace();
                    break;
                case Time :
                    function.addParameter(new DemoParameter(WEATHER_TIME_PARAM_CODE, keyword.keyword));
                    compileHadTimeAndPlace();
                    break;
                default :
                    keyword = kt.next();
            }
        }
    }
    
    private void compileWeatherWithTime() {
        //System.out.println("Compie Weather With Time");
        stage = StageType.WeatherHadTime;
        keyword = kt.next();
        
        while (true) {
            switch (keyword.type) {
                case End :
                    return;
                case Place :
                    function.addParameter(new DemoParameter(WEATHER_PLACE_PARAM_CODE, keyword.keyword));
                    compileCompletedWeather();
                    break;
                default :
                    keyword = kt.next();
            }
        }
    }
    
    private void compileWeatherWithPlace() {
        //System.out.println("Compile Weather With Place");
        stage = StageType.WeatherHadPlace;
        keyword = kt.next();
        
        while (true) {
            switch (keyword.type) {
                case End :
                    return;
                case Time :
                    function.addParameter(new DemoParameter(WEATHER_TIME_PARAM_CODE, keyword.keyword));
                    compileCompletedWeather();
                    break;
                default :
                    keyword = kt.next();
            }
        }
    }
    
    private void compileCompletedWeather() {
        //System.out.println("Compile Completed Weather");
        stage = StageType.CompletedWeather;
        
        while (kt.hasNext()) {
            keyword = kt.next();
        }
    }
    
    private void compileHadTimeAndPlace() {
        //System.out.println("Compile Had Time And Place");
        stage = StageType.HadTimeAndPlace;
        while (true) {
            switch (keyword.type) {
                case End :
                    return;
                case ToWeather :
                	function.setFunction(WEATHER_FUNCTION_CODE, WEATHER_MODULE_CODE);
                    compileCompletedWeather();
                    break;
                default :
                    keyword = kt.next();
            }
        }
    }

}
