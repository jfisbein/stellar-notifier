package com.sputnik.stellar.message;

import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeCreditAlphaNum;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.responses.operations.AccountMergeOperationResponse;
import org.stellar.sdk.responses.operations.AllowTrustOperationResponse;
import org.stellar.sdk.responses.operations.BumpSequenceOperationResponse;
import org.stellar.sdk.responses.operations.ChangeTrustOperationResponse;
import org.stellar.sdk.responses.operations.CreateAccountOperationResponse;
import org.stellar.sdk.responses.operations.CreatePassiveSellOfferOperationResponse;
import org.stellar.sdk.responses.operations.InflationOperationResponse;
import org.stellar.sdk.responses.operations.ManageBuyOfferOperationResponse;
import org.stellar.sdk.responses.operations.ManageDataOperationResponse;
import org.stellar.sdk.responses.operations.ManageSellOfferOperationResponse;
import org.stellar.sdk.responses.operations.OperationResponse;
import org.stellar.sdk.responses.operations.PathPaymentBaseOperationResponse;
import org.stellar.sdk.responses.operations.PathPaymentStrictReceiveOperationResponse;
import org.stellar.sdk.responses.operations.PathPaymentStrictSendOperationResponse;
import org.stellar.sdk.responses.operations.PaymentOperationResponse;
import org.stellar.sdk.responses.operations.SetOptionsOperationResponse;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

public class MessagesCreator {
    public Message createMessage(OperationResponse operation, String accountId) {
        Message message;
        if (operation instanceof PaymentOperationResponse) {
            message = createPaymentMessage((PaymentOperationResponse) operation, accountId);
        } else if (operation instanceof AccountMergeOperationResponse) {
            message = createAccountMergeMessage((AccountMergeOperationResponse) operation);
        } else if (operation instanceof AllowTrustOperationResponse) {
            message = createAllowTrustOperationMessage((AllowTrustOperationResponse) operation);
        } else if (operation instanceof ChangeTrustOperationResponse) {
            message = createChangeTrustOperationMessage((ChangeTrustOperationResponse) operation);
        } else if (operation instanceof CreatePassiveSellOfferOperationResponse) {
            message = createPassiveSellOfferOperationMessage((CreatePassiveSellOfferOperationResponse) operation);
        } else if (operation instanceof InflationOperationResponse) {
            message = createInflationOperationMessage((InflationOperationResponse) operation);
        } else if (operation instanceof ManageDataOperationResponse) {
            message = createManageDataOperationMessage((ManageDataOperationResponse) operation);
        } else if (operation instanceof ManageSellOfferOperationResponse) {
            message = createManageSellOfferOperationMessage((ManageSellOfferOperationResponse) operation);
        } else if (operation instanceof PathPaymentStrictReceiveOperationResponse) {
            message = createPathPaymentOperationMessage((PathPaymentStrictReceiveOperationResponse) operation);
        } else if (operation instanceof PathPaymentStrictSendOperationResponse) {
            message = createPathPaymentOperationMessage((PathPaymentStrictSendOperationResponse) operation);
        } else if (operation instanceof SetOptionsOperationResponse) {
            message = createSetOptionsOperationMessage((SetOptionsOperationResponse) operation);
        } else if (operation instanceof CreateAccountOperationResponse) {
            message = createCreateAccountOperationMessage((CreateAccountOperationResponse) operation);
        } else if (operation instanceof BumpSequenceOperationResponse) {
            message = createBumpSequenceOperationMessage((BumpSequenceOperationResponse) operation);
        } else if (operation instanceof ManageBuyOfferOperationResponse) {
            message = createManageBuyOfferOperationResponseMessage((ManageBuyOfferOperationResponse) operation);
        } else if (operation instanceof PathPaymentBaseOperationResponse) {
            message = createPathPaymentBaseOperationResponse((PathPaymentBaseOperationResponse) operation);
        } else {
            message = createUnknownOperationTypeMessage(operation);
        }

        return message;
    }

    private Message createPathPaymentBaseOperationResponse(PathPaymentBaseOperationResponse operation) {
        String amount = operation.getAmount();
        String asset = getAssetName(operation.getAsset());
        String from = operation.getFrom();
        String to = operation.getTo();
        String sourceAmount = operation.getSourceAmount();
        String sourceAsset = getAssetName(operation.getSourceAsset());
        String path = operation.getPath().stream().map(this::getAssetName).collect(Collectors.joining(", "));

        String subject = "Path Payment Base Operation";
        String body = String.format("Path Payment Base Operation. Asset: %s, From: %s, To: %s, Amount: %s, Source Amount: %s, Source Asset: %s, Path: %s ",
                asset, from, to, amount, sourceAmount, sourceAsset, path);

        return new Message(subject, body);
    }

    private Message createManageBuyOfferOperationResponseMessage(ManageBuyOfferOperationResponse operation) {
        String amount = operation.getAmount();
        String buyingAsset = getAssetName(operation.getBuyingAsset());
        Integer offerId = operation.getOfferId();
        String price = operation.getPrice();
        String sellingAsset = getAssetName(operation.getSellingAsset());

        String subject = "Manage Buy Offer Operation";
        String body = String.format("Buy offer operation. offerId: %s, Buying Asset: %s, Amount: %s, Selling Asset: %s, Price: %s",
                offerId, buyingAsset, amount, sellingAsset, price);

        return new Message(subject, body);
    }

    private Message createBumpSequenceOperationMessage(BumpSequenceOperationResponse operation) {
        Long bumpTo = operation.getBumpTo();

        String subject = "Stellar Bump Sequence";
        String body = String.format("Bumped Sequence to %s", bumpTo);

        return new Message(subject, body);
    }

    private Message createCreateAccountOperationMessage(CreateAccountOperationResponse createAccountOperation) {
        String account = createAccountOperation.getAccount();
        String funder = createAccountOperation.getFunder();
        String startingBalance = createAccountOperation.getStartingBalance();

        String subject = "Stellar account created";
        String body = String.format("Account %s created by funder %s with balance %s", account, funder, startingBalance);

        return new Message(subject, body);
    }

    private Message createUnknownOperationTypeMessage(OperationResponse operation) {
        String subject = "Stellar Unknown operation type.";
        String body = String.format("Operation Received - Type: %s, Id: %s, SourceAccount: %s", operation.getType(), operation.getId(), operation.getSourceAccount());

        return new Message(subject, body);
    }

    private Message createSetOptionsOperationMessage(SetOptionsOperationResponse setOptionsOperation) {
        String[] clearFlags = setOptionsOperation.getClearFlags();
        Integer highThreshold = setOptionsOperation.getHighThreshold();
        String homeDomain = setOptionsOperation.getHomeDomain();
        String inflationDestination = setOptionsOperation.getInflationDestination();
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

    private Message createPathPaymentOperationMessage(PathPaymentBaseOperationResponse pathPaymentOperation) {
        String amount = pathPaymentOperation.getAmount();
        String asset = getAssetName(pathPaymentOperation.getAsset());
        String from = pathPaymentOperation.getFrom();
        String to = pathPaymentOperation.getTo();
        String body = String.format("Created path payment of %s %s, from %s to %s", amount, asset, from, to);
        String subject = "Stellar Path Payment operation";

        return new Message(subject, body);
    }

    private Message createManageSellOfferOperationMessage(ManageSellOfferOperationResponse manageSellOfferOperation) {
        String amount = manageSellOfferOperation.getAmount();
        String buyingAsset = getAssetName(manageSellOfferOperation.getBuyingAsset());
        Integer offerId = manageSellOfferOperation.getOfferId();
        String price = manageSellOfferOperation.getPrice();
        String sellingAsset = getAssetName(manageSellOfferOperation.getSellingAsset());

        String body = String.format("Managed sell offer buy %s %s at %s using %s with id %s", amount, buyingAsset, price, sellingAsset, offerId);
        String subject = "Stellar manage sell offer operation.";

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

    private Message createPassiveSellOfferOperationMessage(CreatePassiveSellOfferOperationResponse createPassiveSellOfferOperation) {
        String amount = createPassiveSellOfferOperation.getAmount();
        String buyingAsset = getAssetName(createPassiveSellOfferOperation.getBuyingAsset());
        String price = createPassiveSellOfferOperation.getPrice();
        String sellingAsset = getAssetName(createPassiveSellOfferOperation.getSellingAsset());
        Integer offerId = createPassiveSellOfferOperation.getOfferId();

        String body = String.format("Created passive sell offer buy %s %s at %s using %s with id %s", amount, buyingAsset, price, sellingAsset, offerId);
        String subject = "Stellar passive sell offer created.";

        return new Message(subject, body);

    }

    private Message createChangeTrustOperationMessage(ChangeTrustOperationResponse changeTrustOperation) {
        String asset = getAssetName(changeTrustOperation.getAsset());
        String trustee = changeTrustOperation.getTrustee();
        String trustor = changeTrustOperation.getTrustor();
        String limit = changeTrustOperation.getLimit();

        String body = String.format("Changed trust, from %s, to %s on %s with limit %s", trustor, trustee, asset, limit);
        String subject = "Stellar change trust";

        return new Message(subject, body);
    }

    private Message createAllowTrustOperationMessage(AllowTrustOperationResponse allowTrustOperation) {
        String asset = getAssetName(allowTrustOperation.getAsset());
        String trustee = allowTrustOperation.getTrustee();
        String trustor = allowTrustOperation.getTrustor();

        String body = String.format("Trust allowed from %s to %s on %s.", trustor, trustee, asset);
        String subject = "Stellar allow trust.";

        return new Message(subject, body);
    }

    private Message createAccountMergeMessage(AccountMergeOperationResponse accountMergeOperation) {
        String accountId = accountMergeOperation.getAccount();
        String into = accountMergeOperation.getInto();
        String body = String.format("Account %s merged into %s.", accountId, into);
        String subject = "Stellar merge account.";

        return new Message(subject, body);
    }

    private Message createPaymentMessage(PaymentOperationResponse paymentOperation, String accountId) {
        String amount = paymentOperation.getAmount();
        String asset = getAssetName(paymentOperation.getAsset());
        String from = paymentOperation.getFrom();
        String to = paymentOperation.getTo();
        Date date = Date.from(Instant.parse(paymentOperation.getCreatedAt()));
        String subject = "Stellar payment operation.";
        String body;
        if (paymentOperation.getTo().equals(accountId)) {
            body = String.format("Received payment of %s %s from %s to %s on %tc.", amount, asset, from, to, date);
        } else {
            body = String.format("Sent payment of %s %s from %s to %s on %tc.", amount, asset, from, to, date);
        }

        return new Message(subject, body);
    }

    private String getAssetName(Asset asset) {
        String assetName;
        if (asset instanceof AssetTypeNative) {
            assetName = "lumens";
        } else if (asset instanceof AssetTypeCreditAlphaNum) {
            assetName = ((AssetTypeCreditAlphaNum) asset).getCode();
            assetName += ":";
            assetName += ((AssetTypeCreditAlphaNum) asset).getIssuer();
        } else {
            assetName = "unknown";
        }

        return assetName;
    }
}
