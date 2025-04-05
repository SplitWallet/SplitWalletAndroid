package com.example.splitwallet.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "Envelope", strict = false)
public class CbrResponse {
    @Element(name = "Body")
    private Body body;

    public List<ValuteCurs> getValutes() {
        return body.getResponse().getResult().getDiffgram().getValuteData().getValutes();
    }

    @Root(name = "Body", strict = false)
    public static class Body {
        @Element(name = "GetCursOnDateResponse")
        private Response response;

        public Response getResponse() {
            return response;
        }
    }

    @Root(name = "GetCursOnDateResponse", strict = false)
    public static class Response {
        @Element(name = "GetCursOnDateResult")
        private Result result;

        public Result getResult() {
            return result;
        }
    }

    @Root(name = "GetCursOnDateResult", strict = false)
    public static class Result {
        @Element(name = "diffgram")
        private Diffgram diffgram;

        public Diffgram getDiffgram() {
            return diffgram;
        }
    }

    @Root(name = "diffgram", strict = false)
    public static class Diffgram {
        @Element(name = "ValuteData")
        private ValuteData valuteData;

        public ValuteData getValuteData() {
            return valuteData;
        }
    }

    @Root(name = "ValuteData", strict = false)
    public static class ValuteData {
        @ElementList(inline = true)
        private List<ValuteCurs> valutes;

        public List<ValuteCurs> getValutes() {
            return valutes;
        }
    }
}

