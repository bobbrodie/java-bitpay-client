/*
 * Copyright (c) 2019 BitPay
 */

package com.bitpay.sdk.client;

import com.bitpay.sdk.exceptions.BitPayException;
import com.bitpay.sdk.exceptions.PayoutRecipientCancellationException;
import com.bitpay.sdk.exceptions.PayoutRecipientCreationException;
import com.bitpay.sdk.exceptions.PayoutRecipientNotificationException;
import com.bitpay.sdk.exceptions.PayoutRecipientQueryException;
import com.bitpay.sdk.exceptions.PayoutRecipientUpdateException;
import com.bitpay.sdk.model.Facade;
import com.bitpay.sdk.model.Payout.PayoutRecipient;
import com.bitpay.sdk.model.Payout.PayoutRecipients;
import com.bitpay.sdk.util.AccessTokens;
import com.bitpay.sdk.util.JsonMapperFactory;
import com.bitpay.sdk.util.UuidGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;

public class PayoutRecipientsClient {

    private final BitPayClient bitPayClient;
    private final AccessTokens accessTokens;
    private final UuidGenerator uuidGenerator;

    public PayoutRecipientsClient(
        BitPayClient bitPayClient,
        AccessTokens accessTokens,
        UuidGenerator uuidGenerator
    ) {
        this.bitPayClient = bitPayClient;
        this.accessTokens = accessTokens;
        this.uuidGenerator = uuidGenerator;
    }

    /**
     * Submit BitPay Payout Recipients.
     *
     * @param recipients PayoutRecipients A PayoutRecipients object with request parameters defined.
     * @return array A list of BitPay PayoutRecipients objects..
     * @throws BitPayException                  BitPayException class
     * @throws PayoutRecipientCreationException PayoutRecipientCreationException class
     */
    public List<PayoutRecipient> submitPayoutRecipients(PayoutRecipients recipients)
        throws BitPayException,
        PayoutRecipientCreationException {
        recipients.setToken(this.accessTokens.getAccessToken(Facade.PAYOUT));
        recipients.setGuid(this.uuidGenerator.execute());
        JsonMapper mapper = JsonMapperFactory.create();
        String json;

        try {
            json = mapper.writeValueAsString(recipients);
        } catch (JsonProcessingException e) {
            throw new PayoutRecipientCreationException(null,
                "failed to serialize PayoutRecipients object : " + e.getMessage());
        }

        List<PayoutRecipient> recipientsList;

        try {
            HttpResponse response = this.bitPayClient.post("recipients", json, true);
            recipientsList = Arrays
                .asList(new ObjectMapper()
                    .readValue(this.bitPayClient.responseToJsonString(response), PayoutRecipient[].class));
        } catch (JsonProcessingException e) {
            throw new PayoutRecipientCreationException(null,
                "failed to deserialize BitPay server response (PayoutRecipients) : " + e.getMessage());
        } catch (Exception e) {
            throw new PayoutRecipientCreationException(null,
                "failed to deserialize BitPay server response (PayoutRecipients) : " + e.getMessage());
        }

        return recipientsList;
    }

    /**
     * Retrieve a collection of BitPay Payout Recipients.
     *
     * @param status String|null The recipient status you want to query on.
     * @param limit  int Maximum results that the query will return (useful for
     *               paging results). result).
     * @param offset int Offset for paging.
     * @return array A list of BitPayRecipient objects.
     * @throws BitPayException               BitPayException class
     * @throws PayoutRecipientQueryException PayoutRecipientQueryException class
     */
    public List<PayoutRecipient> getPayoutRecipients(String status, Integer limit, Integer offset)
        throws BitPayException, PayoutRecipientQueryException {

        final List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("token", this.accessTokens.getAccessToken(Facade.PAYOUT)));
        if (status != null) {
            params.add(new BasicNameValuePair("status", status));
        }
        if (limit != null) {
            params.add(new BasicNameValuePair("limit", limit.toString()));
        }
        if (offset != null) {
            params.add(new BasicNameValuePair("offset", offset.toString()));
        }

        List<PayoutRecipient> recipientsList;

        try {
            HttpResponse response = this.bitPayClient.get("recipients", params, true);
            recipientsList = Arrays
                .asList(new ObjectMapper()
                    .readValue(this.bitPayClient.responseToJsonString(response), PayoutRecipient[].class));
        } catch (JsonProcessingException e) {
            throw new PayoutRecipientQueryException(null,
                "failed to deserialize BitPay server response (PayoutRecipients) : " + e.getMessage());
        } catch (Exception e) {
            throw new PayoutRecipientQueryException(null,
                "failed to deserialize BitPay server response (PayoutRecipients) : " + e.getMessage());
        }

        return recipientsList;
    }

    /**
     * Retrieve a BitPay payout recipient by batch id using.  The client must have been previously authorized for the
     * payout facade.
     *
     * @param recipientId String The id of the recipient to retrieve.
     * @return PayoutRecipient A BitPay PayoutRecipient object.
     * @throws BitPayException               BitPayException class
     * @throws PayoutRecipientQueryException PayoutRecipientQueryException class
     */
    public PayoutRecipient getPayoutRecipient(String recipientId)
        throws BitPayException, PayoutRecipientQueryException {
        final List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("token", this.accessTokens.getAccessToken(Facade.PAYOUT)));

        PayoutRecipient recipient;

        try {
            HttpResponse response = this.bitPayClient.get("recipients/" + recipientId, params, true);
            recipient =
                new ObjectMapper().readValue(this.bitPayClient.responseToJsonString(response), PayoutRecipient.class);
        } catch (JsonProcessingException e) {
            throw new PayoutRecipientQueryException(null,
                "failed to deserialize BitPay server response (PayoutRecipient) : " + e.getMessage());
        } catch (Exception e) {
            throw new PayoutRecipientQueryException(null,
                "failed to deserialize BitPay server response (PayoutRecipient) : " + e.getMessage());
        }

        return recipient;
    }

    /**
     * Update a Payout Recipient.
     *
     * @param recipientId String The recipient id for the recipient to be updated.
     * @param recipient   PayoutRecipients A PayoutRecipient object with updated
     *                    parameters defined.
     * @return The updated recipient object.
     * @throws BitPayException                BitPayException class
     * @throws PayoutRecipientUpdateException PayoutRecipientUpdateException class
     */
    public PayoutRecipient updatePayoutRecipient(String recipientId, PayoutRecipient recipient)
        throws BitPayException, PayoutRecipientUpdateException {
        recipient.setToken(this.accessTokens.getAccessToken(Facade.PAYOUT));
        recipient.setGuid(this.uuidGenerator.execute());
        JsonMapper mapper = JsonMapperFactory.create();
        String json;

        try {
            json = mapper.writeValueAsString(recipient);
        } catch (JsonProcessingException e) {
            throw new PayoutRecipientUpdateException(null,
                "failed to serialize PayoutRecipient object : " + e.getMessage());
        }

        PayoutRecipient updateRecipient;

        try {
            HttpResponse response = this.bitPayClient.update("recipients/" + recipientId, json);
            updateRecipient =
                new ObjectMapper().readValue(this.bitPayClient.responseToJsonString(response), PayoutRecipient.class);
        } catch (JsonProcessingException e) {
            throw new PayoutRecipientUpdateException(null,
                "failed to deserialize BitPay server response (PayoutRecipients) : " + e.getMessage());
        } catch (Exception e) {
            throw new PayoutRecipientUpdateException(null,
                "failed to deserialize BitPay server response (PayoutRecipients) : " + e.getMessage());
        }

        return updateRecipient;
    }

    /**
     * Cancel a BitPay Payout recipient.
     *
     * @param recipientId String The id of the recipient to cancel.
     * @return True if the delete operation was successfull, false otherwise.
     * @throws BitPayException                      BitPayException class
     * @throws PayoutRecipientCancellationException PayoutRecipientCancellationException
     *                                              class
     */
    public Boolean deletePayoutRecipient(String recipientId)
        throws BitPayException, PayoutRecipientCancellationException {

        final List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("token", this.accessTokens.getAccessToken(Facade.PAYOUT)));

        JsonMapper mapper = JsonMapperFactory.create();
        Boolean result;

        try {
            HttpResponse response = this.bitPayClient.delete("recipients/" + recipientId, params);
            String jsonString = this.bitPayClient.responseToJsonString(response);
            JsonNode rootNode = mapper.readTree(jsonString);
            JsonNode node = rootNode.get("status");
            result = node.toString().replace("\"", "").toLowerCase(Locale.ROOT).equals("success");
        } catch (BitPayException ex) {
            throw new PayoutRecipientCancellationException(ex.getStatusCode(), ex.getReasonPhrase());
        } catch (Exception e) {
            throw new PayoutRecipientCancellationException(null,
                "failed to deserialize BitPay server response (PayoutRecipients) : " + e.getMessage());
        }

        return result;
    }

    /**
     * Request a payout recipient notification
     *
     * @param recipientId String A BitPay recipient ID.
     * @return True if the notification was successfully sent, false otherwise.
     * @throws BitPayException                      BitPayException class
     * @throws PayoutRecipientNotificationException PayoutRecipientNotificationException
     *                                              class
     */
    public Boolean requestPayoutRecipientNotification(String recipientId)
        throws PayoutRecipientNotificationException, BitPayException {
        final Map<String, String> params = new HashMap<>();
        params.put("token", this.accessTokens.getAccessToken(Facade.PAYOUT));

        JsonMapper mapper = JsonMapperFactory.create();
        Boolean result;
        String json;

        try {
            json = mapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            throw new PayoutRecipientNotificationException(null,
                "failed to serialize PayoutRecipient object : " + e.getMessage());
        }

        try {
            HttpResponse response = this.bitPayClient.post("recipients/" + recipientId + "/notifications", json, true);
            String jsonString = this.bitPayClient.responseToJsonString(response);
            JsonNode rootNode = mapper.readTree(jsonString);
            JsonNode node = rootNode.get("status");
            result = node.toString().replace("\"", "").toLowerCase(Locale.ROOT).equals("success");
        } catch (BitPayException ex) {
            throw new PayoutRecipientNotificationException(ex.getStatusCode(), ex.getReasonPhrase());
        } catch (Exception e) {
            throw new PayoutRecipientNotificationException(null,
                "failed to deserialize BitPay server response (PayoutRecipients) : " + e.getMessage());
        }

        return result;
    }
}
