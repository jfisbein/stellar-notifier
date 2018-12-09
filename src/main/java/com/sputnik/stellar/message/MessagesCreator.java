package com.sputnik.stellar.message;

import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeCreditAlphaNum;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.responses.operations.AccountMergeOperationResponse;
import org.stellar.sdk.responses.operations.AllowTrustOperationResponse;
import org.stellar.sdk.responses.operations.BumpSequenceOperationResponse;
import org.stellar.sdk.responses.operations.ChangeTrustOperationResponse;
import org.stellar.sdk.responses.operations.CreateAccountOperationResponse;
import org.stellar.sdk.responses.operations.CreatePassiveOfferOperationResponse;
import org.stellar.sdk.responses.operations.InflationOperationResponse;
import org.stellar.sdk.responses.operations.ManageDataOperationResponse;
import org.stellar.sdk.responses.operations.ManageOfferOperationResponse;
import org.stellar.sdk.responses.operations.OperationResponse;
import org.stellar.sdk.responses.operations.PathPaymentOperationResponse;
import org.stellar.sdk.responses.operations.PaymentOperationResponse;
import org.stellar.sdk.responses.operations.SetOptionsOperationResponse;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

public class MessagesCreator {
    public Message createMessage(OperationResponse operation, KeyPair account) {
        Message message;
        if (operation instanceof PaymentOperationResponse) {
            message = createPaymentMessage((PaymentOperationResponse) operation, account);
        } else if (operation instanceof AccountMergeOperationResponse) {
            message = createAccountMergeMessage((AccountMergeOperationResponse) operation);
        } else if (operation instanceof AllowTrustOperationResponse) {
            message = createAllowTrustOperationMessage((AllowTrustOperationResponse) operation);
        } else if (operation instanceof ChangeTrustOperationResponse) {
            message = createChangeTrustOperationMessage((ChangeTrustOperationResponse) operation);
        } else if (operation instanceof CreatePassiveOfferOperationResponse) {
            message = createCreatePassiveOfferOperationMessage((CreatePassiveOfferOperationResponse) operation);
        } else if (operation instanceof InflationOperationResponse) {
            message = createInflationOperationMessage((InflationOperationResponse) operation);
        } else if (operation instanceof ManageDataOperationResponse) {
            message = createManageDataOperationMessage((ManageDataOperationResponse) operation);
        } else if (operation instanceof ManageOfferOperationResponse) {
            message = createManageOfferOperationMessage((ManageOfferOperationResponse) operation);
        } else if (operation instanceof PathPaymentOperationResponse) {
            message = createPathPaymentOperationMessage((PathPaymentOperationResponse) operation);
        } else if (operation instanceof SetOptionsOperationResponse) {
            message = createSetOptionsOperationMessage((SetOptionsOperationResponse) operation);
        } else if (operation instanceof CreateAccountOperationResponse) {
            message = createCreateAccountOperationMessage((CreateAccountOperationResponse) operation);
        } else if (operation instanceof BumpSequenceOperationResponse) {
            message = createBumpSequenceOperationMessage((BumpSequenceOperationResponse) operation);
        } else {
            message = createUnknownOperationTypeMessage(operation);
        }

        return message;
    }

    private Message createBumpSequenceOperationMessage(BumpSequenceOperationResponse operation) {
        Long bumpTo = operation.getBumpTo();

        String subject = "Stellar Bump Sequence";
        String body = String.format("Bumped Sequence to %s", bumpTo);

        return new Message(subject, body);
    }

    private Message createCreateAccountOperationMessage(CreateAccountOperationResponse createAccountOperation) {
        String account = createAccountOperation.getAccount().getAccountId();
        String funder = createAccountOperation.getFunder().getAccountId();
        String startingBalance = createAccountOperation.getStartingBalance();

        String subject = "Stellar account created";
        String body = String.format("Account %s created by funder %s with balance %s", account, funder, startingBalance);

        return new Message(subject, body);
    }

    private Message createUnknownOperationTypeMessage(OperationResponse operation) {
        String subject = "Stellar Unknown operation type.";
        String body = String.format("Operation Received - Type: %s, Id: %s, SourceAccount: %s", operation.getType(), operation.getId(), operation.getSourceAccount().getAccountId());

        return new Message(subject, body);
    }

    private Message createSetOptionsOperationMessage(SetOptionsOperationResponse setOptionsOperation) {
        String[] clearFlags = setOptionsOperation.getClearFlags();
        Integer highThreshold = setOptionsOperation.getHighThreshold();
        String homeDomain = setOptionsOperation.getHomeDomain();
        String inflationDestination = setOptionsOperation.getInflationDestination().getAccountId();
        Integer lowThreshold = setOptionsOperation.getLowThreshold();
        Integer masterKeyWeight = setOptionsOperation.getMasterKeyWeight();
        Integer medThreshold = setOptionsOperation.getMedThreshold();
        String[] setFlags = setOptionsOperation.getSetFlags();
        String signer = setOptionsOperation.getSignerKey();
        Integer signerWeight = setOptionsOperation.getSignerWeight();

        String subject = "Stellar Set Options operation";
        String body = String.format("Set options. clearFlags: %s, highThreshold: %s, homeDomain: %s, inflationDestination: %s, " +
                        "lowThreshold: %s, masterKeyWeight: %s, medThreshold: %s, setFlags: %s, signer: %s, signerWeight: %s.",
                Arrays.toString(clearFlags), highThreshold, homeDomain, inflationDestination,
                lowThreshold, masterKeyWeight, medThreshold, Arrays.toString(setFlags), signer, signerWeight);

        return new Message(subject, body);
    }

    private Message createPathPaymentOperationMessage(PathPaymentOperationResponse pathPaymentOperation) {
        String amount = pathPaymentOperation.getAmount();
        String asset = getAssetName(pathPaymentOperation.getAsset());
        String from = pathPaymentOperation.getFrom().getAccountId();
        String to = pathPaymentOperation.getTo().getAccountId();
        String body = String.format("Created path payment of %s %s, from %s to %s", amount, asset, from, to);
        String subject = "Stellar Path Payment operation";

        return new Message(subject, body);
    }

    private Message createManageOfferOperationMessage(ManageOfferOperationResponse manageOfferOperation) {
        String amount = manageOfferOperation.getAmount();
        String buyingAsset = getAssetName(manageOfferOperation.getBuyingAsset());
        Integer offerId = manageOfferOperation.getOfferId();
        String price = manageOfferOperation.getPrice();
        String sellingAsset = getAssetName(manageOfferOperation.getSellingAsset());

        String body = String.format("Managed offer buy %s %s at %s using %s with id %s", amount, buyingAsset, price, sellingAsset, offerId);
        String subject = "Stellar manage offer operation.";

        return new Message(subject, body);
    }

    private Message createManageDataOperationMessage(ManageDataOperationResponse manageDataOperation) {
        String name = manageDataOperation.getName();
        String value = manageDataOperation.getValue();

        String body = String.format("Data Operation %s -> %s", name, value);
        String subject = "Stellar Data Operation";

        return new Message(subject, body);
    }

    private Message createInflationOperationMessage(InflationOperationResponse inflationOperation) {
        return createUnknownOperationTypeMessage(inflationOperation);
    }

    private Message createCreatePassiveOfferOperationMessage(CreatePassiveOfferOperationResponse createPassiveOfferOperation) {
        String amount = createPassiveOfferOperation.getAmount();
        String buyingAsset = getAssetName(createPassiveOfferOperation.getBuyingAsset());
        String price = createPassiveOfferOperation.getPrice();
        String sellingAsset = getAssetName(createPassiveOfferOperation.getSellingAsset());
        Integer offerId = createPassiveOfferOperation.getOfferId();

        String body = String.format("Created passive offer buy %s %s at %s using %s with id %s", amount, buyingAsset, price, sellingAsset, offerId);
        String subject = "Stellar passive offer created.";

        return new Message(subject, body);

    }

    private Message createChangeTrustOperationMessage(ChangeTrustOperationResponse changeTrustOperation) {
        String asset = getAssetName(changeTrustOperation.getAsset());
        String trustee = changeTrustOperation.getTrustee().getAccountId();
        String trustor = changeTrustOperation.getTrustor().getAccountId();
        String limit = changeTrustOperation.getLimit();

        String body = String.format("Changed trust, from %s, to %s on %s with limit %s", trustor, trustee, asset, limit);
        String subject = "Stellar change trust";

        return new Message(subject, body);
    }

    private Message createAllowTrustOperationMessage(AllowTrustOperationResponse allowTrustOperation) {
        String asset = getAssetName(allowTrustOperation.getAsset());
        String trustee = allowTrustOperation.getTrustee().getAccountId();
        String trustor = allowTrustOperation.getTrustor().getAccountId();

        String body = String.format("Trust allowed from %s to %s on %s.", trustor, trustee, asset);
        String subject = "Stellar allow trust.";

        return new Message(subject, body);
    }

    private Message createAccountMergeMessage(AccountMergeOperationResponse accountMergeOperation) {
        String accountId = accountMergeOperation.getAccount().getAccountId();
        String into = accountMergeOperation.getInto().getAccountId();
        String body = String.format("Account %s merged into %s.", accountId, into);
        String subject = "Stellar merge account.";

        return new Message(subject, body);
    }

    private Message createPaymentMessage(PaymentOperationResponse paymentOperation, KeyPair account) {
        String amount = paymentOperation.getAmount();
        String asset = getAssetName(paymentOperation.getAsset());
        String from = paymentOperation.getFrom().getAccountId();
        String to = paymentOperation.getTo().getAccountId();
        Date date = Date.from(Instant.parse(paymentOperation.getCreatedAt()));
        String subject = "Stellar payment operation.";
        String body;
        if (paymentOperation.getTo().getAccountId().equals(account.getAccountId())) {
            body = String.format("Received payment of %s %s from %s to %s on %tc.", amount, asset, from, to, date);
        } else {
            body = String.format("Sent payment of %s %s from %s to %s on %tc.", amount, asset, from, to, date);
        }

        return new Message(subject, body);
    }

    private String getAssetName(Asset asset) {
        String assetName;
        if (asset.equals(new AssetTypeNative())) {
            assetName = "lumens";
        } else {
            assetName = ((AssetTypeCreditAlphaNum) asset).getCode();
            assetName += ":";
            assetName += ((AssetTypeCreditAlphaNum) asset).getIssuer().getAccountId();
        }

        return assetName;
    }
}
