package com.example.lizzie.trafficviz;

/**
 * Created by lizzie on 7/26/16.
 */
import org.json.JSONObject;
import org.json.JSONException;
public class PnGcmMessage extends JSONObject {

        /**
         * Constructor for PnGcmMessage
         */
        public PnGcmMessage() {
            super();
        }

        /**
         * Constructor for PnGcmMessage
         * @param json
         *         json object to be set as data for GCM message
         */
        public PnGcmMessage(JSONObject json) {
            super();
            setData(json);
        }

        /**
         * Set Data for PnGcmMessage
         * @param json
         *         json object to be set as data for GCM message
         */
        public void setData(JSONObject json) {
            try {
                this.put("data", json);
            } catch (JSONException e) {

            }
        }

    }
