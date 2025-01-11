package com.sputnik.stellar.message;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeCreditAlphaNum;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.Memo;
import org.stellar.sdk.MemoText;
import org.stellar.sdk.responses.AssetAmount;
import org.stellar.sdk.responses.Claimant;
import org.stellar.sdk.responses.Price;
import org.stellar.sdk.responses.operations.AccountMergeOperationResponse;
import org.stellar.sdk.responses.operations.BeginSponsoringFutureReservesOperationResponse;
import org.stellar.sdk.responses.operations.BumpSequenceOperationResponse;
import org.stellar.sdk.responses.operations.ChangeTrustOperationResponse;
import org.stellar.sdk.responses.operations.ClaimClaimableBalanceOperationResponse;
import org.stellar.sdk.responses.operations.ClawbackClaimableBalanceOperationResponse;
import org.stellar.sdk.responses.operations.ClawbackOperationResponse;
import org.stellar.sdk.responses.operations.CreateAccountOperationResponse;
import org.stellar.sdk.responses.operations.CreateClaimableBalanceOperationResponse;
import org.stellar.sdk.responses.operations.CreatePassiveSellOfferOperationResponse;
import org.stellar.sdk.responses.operations.EndSponsoringFutureReservesOperationResponse;
import org.stellar.sdk.responses.operations.ExtendFootprintTTLOperationResponse;
import org.stellar.sdk.responses.operations.InflationOperationResponse;
import org.stellar.sdk.responses.operations.InvokeHostFunctionOperationResponse;
import org.stellar.sdk.responses.operations.InvokeHostFunctionOperationResponse.AssetContractBalanceChange;
import org.stellar.sdk.responses.operations.InvokeHostFunctionOperationResponse.HostFunctionParameter;
import org.stellar.sdk.responses.operations.LiquidityPoolDepositOperationResponse;
import org.stellar.sdk.responses.operations.LiquidityPoolWithdrawOperationResponse;
import org.stellar.sdk.responses.operations.ManageBuyOfferOperationResponse;
import org.stellar.sdk.responses.operations.ManageDataOperationResponse;
import org.stellar.sdk.responses.operations.ManageSellOfferOperationResponse;
import org.stellar.sdk.responses.operations.OperationResponse;
import org.stellar.sdk.responses.operations.PathPaymentBaseOperationResponse;
import org.stellar.sdk.responses.operations.PathPaymentStrictReceiveOperationResponse;
import org.stellar.sdk.responses.operations.PathPaymentStrictSendOperationResponse;
import org.stellar.sdk.responses.operations.PaymentOperationResponse;
import org.stellar.sdk.responses.operations.RestoreFootprintOperationResponse;
import org.stellar.sdk.responses.operations.RevokeSponsorshipOperationResponse;
import org.stellar.sdk.responses.operations.SetOptionsOperationResponse;
import org.stellar.sdk.responses.operations.SetTrustLineFlagsOperationResponse;

public class PaymentOperationMessagesCreator {

  private final List<String> excludedTokens;

  public PaymentOperationMessagesCreator(List<String> excludedTokens) {
    this.excludedTokens = excludedTokens;
  }

  public Message createMessage(OperationResponse operation, String accountId) {
    Message message;
    if (operation instanceof PaymentOperationResponse paymentOperationResponse) {
      message = createPaymentMessage(paymentOperationResponse, accountId);
    } else if (operation instanceof AccountMergeOperationResponse accountMergeOperationResponse) {
      message = createAccountMergeMessage(accountMergeOperationResponse);
    } else if (operation instanceof ChangeTrustOperationResponse changeTrustOperationResponse) {
      message = createChangeTrustOperationMessage(changeTrustOperationResponse);
    } else if (operation instanceof CreatePassiveSellOfferOperationResponse createPassiveSellOfferOperationResponse) {
      message = createPassiveSellOfferOperationMessage(createPassiveSellOfferOperationResponse);
    } else if (operation instanceof InflationOperationResponse inflationOperationResponse) {
      message = createInflationOperationMessage(inflationOperationResponse);
    } else if (operation instanceof ManageDataOperationResponse manageDataOperationResponse) {
      message = createManageDataOperationMessage(manageDataOperationResponse);
    } else if (operation instanceof ManageSellOfferOperationResponse manageSellOfferOperationResponse) {
      message = createManageSellOfferOperationMessage(manageSellOfferOperationResponse);
    } else if (operation instanceof PathPaymentStrictReceiveOperationResponse pathPaymentStrictReceiveOperationResponse) {
      message = createPathPaymentOperationMessage(pathPaymentStrictReceiveOperationResponse);
    } else if (operation instanceof PathPaymentStrictSendOperationResponse pathPaymentStrictSendOperationResponse) {
      message = createPathPaymentOperationMessage(pathPaymentStrictSendOperationResponse);
    } else if (operation instanceof SetOptionsOperationResponse setOptionsOperationResponse) {
      message = createSetOptionsOperationMessage(setOptionsOperationResponse);
    } else if (operation instanceof CreateAccountOperationResponse createAccountOperationResponse) {
      message = createCreateAccountOperationMessage(createAccountOperationResponse);
    } else if (operation instanceof BumpSequenceOperationResponse bumpSequenceOperationResponse) {
      message = createBumpSequenceOperationMessage(bumpSequenceOperationResponse);
    } else if (operation instanceof ManageBuyOfferOperationResponse manageBuyOfferOperationResponse) {
      message = createManageBuyOfferOperationResponseMessage(manageBuyOfferOperationResponse);
    } else if (operation instanceof PathPaymentBaseOperationResponse pathPaymentBaseOperationResponse) {
      message = createPathPaymentBaseOperationResponse(pathPaymentBaseOperationResponse);
    } else if (operation instanceof BeginSponsoringFutureReservesOperationResponse beginSponsoringFutureReservesOperationResponse) {
      message = createBeginSponsoringFutureReservesOperationResponseMessage(beginSponsoringFutureReservesOperationResponse);
    } else if (operation instanceof ClaimClaimableBalanceOperationResponse claimClaimableBalanceOperationResponse) {
      message = createClaimClaimableBalanceOperationResponseMessage(claimClaimableBalanceOperationResponse);
    } else if (operation instanceof ClawbackClaimableBalanceOperationResponse clawbackClaimableBalanceOperationResponse) {
      message = createClawbackClaimableBalanceOperationResponseMessage(clawbackClaimableBalanceOperationResponse);
    } else if (operation instanceof ClawbackOperationResponse clawbackOperationResponse) {
      message = createClawbackOperationResponseMessage(clawbackOperationResponse);
    } else if (operation instanceof CreateClaimableBalanceOperationResponse createClaimableBalanceOperationResponse) {
      message = createCreateClaimableBalanceOperationResponseMessage(createClaimableBalanceOperationResponse);
    } else if (operation instanceof EndSponsoringFutureReservesOperationResponse endSponsoringFutureReservesOperationResponse) {
      message = createEndSponsoringFutureReservesOperationResponseMessage(endSponsoringFutureReservesOperationResponse);
    } else if (operation instanceof ExtendFootprintTTLOperationResponse extendFootprintTTLOperationResponse) {
      message = createExtendFootprintTTLOperationResponseMessage(extendFootprintTTLOperationResponse);
    } else if (operation instanceof InvokeHostFunctionOperationResponse invokeHostFunctionOperationResponse) {
      message = createInvokeHostFunctionOperationResponseMessage(invokeHostFunctionOperationResponse);
    } else if (operation instanceof LiquidityPoolDepositOperationResponse liquidityPoolDepositOperationResponse) {
      message = createLiquidityPoolDepositOperationResponseMessage(liquidityPoolDepositOperationResponse);
    } else if (operation instanceof LiquidityPoolWithdrawOperationResponse liquidityPoolWithdrawOperationResponse) {
      message = createLiquidityPoolWithdrawOperationResponseMessage(liquidityPoolWithdrawOperationResponse);
    } else if (operation instanceof RestoreFootprintOperationResponse restoreFootprintOperationResponse) {
      message = createRestoreFootprintOperationResponseMessage(restoreFootprintOperationResponse);
    } else if (operation instanceof RevokeSponsorshipOperationResponse revokeSponsorshipOperationResponse) {
      message = createRevokeSponsorshipOperationResponseMessage(revokeSponsorshipOperationResponse);
    } else if (operation instanceof SetTrustLineFlagsOperationResponse setTrustLineFlagsOperationResponse) {
      message = createSetTrustLineFlagsOperationResponseMessage(setTrustLineFlagsOperationResponse);
    } else {
      message = createUnknownOperationTypeMessage(operation);
    }

    return message;
  }

  private Message createSetTrustLineFlagsOperationResponseMessage(SetTrustLineFlagsOperationResponse operation) {
    String asset = getAssetName(operation.getAsset());
    String trustor = operation.getTrustor();
    List<String> clearFlagStrings = operation.getClearFlagStrings();

    String subject = "Stellar Set Trust Line Flags";
    String body = String.format("Set Trust Line Flags. Asset: %s, Trustor: %s, Clear Flags: %s", asset, trustor, clearFlagStrings);
    return new Message(subject, body);
  }

  private Message createRevokeSponsorshipOperationResponseMessage(RevokeSponsorshipOperationResponse operation) {
    String accountId = operation.getAccountId();
    String claimableBalanceId = operation.getClaimableBalanceId();
    String dataAccountId = operation.getDataAccountId();
    String dataName = operation.getDataName();
    Long offerId = operation.getOfferId();
    String trustlineAccountId = operation.getTrustlineAccountId();
    String trustlineAsset = operation.getTrustlineAsset();
    String signerAccountId = operation.getSignerAccountId();
    String signerKey = operation.getSignerKey();

    String subject = "Stellar Revoke Sponsorship";
    String body = String.format(
      "Revoke Sponsorship. Account Id: %s, Claimable Balance Id: %s, Data Account Id: %s, Data Name: %s, Offer Id: %s, Trustline Account Id: %s, Trustline Asset: %s, Signer Account Id: %s, Signer Key: %s",
      accountId, claimableBalanceId, dataAccountId, dataName, offerId, trustlineAccountId, trustlineAsset, signerAccountId, signerKey);

    return new Message(subject, body);
  }

  private Message createRestoreFootprintOperationResponseMessage(RestoreFootprintOperationResponse operation) {
    String subject = "Stellar Restore Footprint";
    String body = "Restored Footprint";
    return new Message(subject, body);
  }

  private Message createLiquidityPoolWithdrawOperationResponseMessage(LiquidityPoolWithdrawOperationResponse operation) {
    String liquidityPoolId = operation.getLiquidityPoolId();
    List<AssetAmount> reservesMin = operation.getReservesMin();
    List<AssetAmount> reservesReceived = operation.getReservesReceived();
    String shares = operation.getShares();
    String subject = "Stellar Liquidity Pool Withdraw";
    String body = String.format(
      "Liquidity Pool Withdraw. Liquidity Pool Id: %s, Reserves Min: %s, Reserves Received: %s, Shares: %s",
      liquidityPoolId, reservesMin, reservesReceived, shares);
    return new Message(subject, body);
  }

  private Message createLiquidityPoolDepositOperationResponseMessage(LiquidityPoolDepositOperationResponse operation) {
    String liquidityPoolId = operation.getLiquidityPoolId();
    List<AssetAmount> reservesMax = operation.getReservesMax();
    String maxPrice = operation.getMaxPrice();
    String minPrice = operation.getMinPrice();
    Price maxPriceR = operation.getMaxPriceR();
    Price minPriceR = operation.getMinPriceR();
    List<AssetAmount> reservesDeposited = operation.getReservesDeposited();
    String sharesReceived = operation.getSharesReceived();

    String subject = "Stellar Liquidity Pool Deposit";
    String body = String.format(
      "Liquidity Pool Deposit. Liquidity Pool Id: %s, Reserves Max: %s, Max Price: %s, Min Price: %s, Max Price R: %s, Min Price R: %s, Reserves Deposited: %s, Shares Received: %s",
      liquidityPoolId, reservesMax, maxPrice, minPrice, maxPriceR, minPriceR, reservesDeposited, sharesReceived);
    return new Message(subject, body);
  }

  private Message createInvokeHostFunctionOperationResponseMessage(InvokeHostFunctionOperationResponse operation) {
    String function = operation.getFunction();
    List<HostFunctionParameter> parameters = operation.getParameters();
    String address = operation.getAddress();
    String salt = operation.getSalt();
    List<AssetContractBalanceChange> assetBalanceChanges = operation.getAssetBalanceChanges();

    String subject = "Stellar Invoke Host Function";
    String body = String.format("Invoked Host Function %s with parameters %s, address %s, salt %s, asset balance changes %s", function,
      parameters, address, salt, assetBalanceChanges);

    return new Message(subject, body);
  }

  private Message createExtendFootprintTTLOperationResponseMessage(ExtendFootprintTTLOperationResponse operation) {
    Long extendTo = operation.getExtendTo();
    String subject = "Stellar Extend Footprint TTL";
    String body = String.format("Extended Footprint TTL to %s", extendTo);

    return new Message(subject, body);
  }

  private Message createEndSponsoringFutureReservesOperationResponseMessage(EndSponsoringFutureReservesOperationResponse operation) {
    String beginSponsor = operation.getBeginSponsor();
    String subject = "End Sponsoring Future Reserves Operation";
    String body = String.format("End Sponsoring Future Reserves Operation. Begin Sponsor: %s", beginSponsor);
    return new Message(subject, body);
  }

  private Message createCreateClaimableBalanceOperationResponseMessage(CreateClaimableBalanceOperationResponse operation) {
    String amount = operation.getAmount();
    String asset = getAssetName(operation.getAsset());
    List<Claimant> claimants = operation.getClaimants();
    String subject = "Create Claimable Balance Operation";
    String body = String.format("Create Claimable Balance Operation. Asset: %s, Amount: %s, Claimants: %s", asset, amount, claimants);

    return new Message(subject, body);
  }

  private Message createClawbackOperationResponseMessage(ClawbackOperationResponse operation) {
    String amount = operation.getAmount();
    String asset = getAssetName(operation.getAsset());
    String from = operation.getFrom();

    String subject = "Clawback Operation";
    String body = String.format("Clawback Operation. Asset: %s, Amount: %s, From: %s", asset, amount, from);
    return new Message(subject, body);
  }

  private Message createClawbackClaimableBalanceOperationResponseMessage(ClawbackClaimableBalanceOperationResponse operation) {
    String balanceId = operation.getBalanceId();
    String subject = "Clawback Claimable Balance Operation";
    String body = String.format("Clawback Claimable Balance Operation. Balance Id: %s", balanceId);

    return new Message(subject, body);
  }

  private Message createClaimClaimableBalanceOperationResponseMessage(ClaimClaimableBalanceOperationResponse operation) {
    String balanceId = operation.getBalanceId();
    String claimant = operation.getClaimant();
    String subject = "Claim Claimable Balance Operation";
    String body = String.format("Claim Claimable Balance Operation. Balance Id: %s, Claimant: %s", balanceId, claimant);

    return new Message(subject, body);
  }

  private Message createBeginSponsoringFutureReservesOperationResponseMessage(BeginSponsoringFutureReservesOperationResponse operation) {
    String sponsoredId = operation.getSponsoredId();
    String subject = "Begin Sponsoring Future Reserves Operation";
    String body = String.format("Begin Sponsoring Future Reserves Operation. Sponsored Id: %s", sponsoredId);

    return new Message(subject, body);
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
    String body = String.format(
      "Path Payment Base Operation. Asset: %s, From: %s, To: %s, Amount: %s, Source Amount: %s, Source Asset: %s, Path: %s ",
      asset, from, to, amount, sourceAmount, sourceAsset, path);

    return new Message(subject, body);
  }

  private Message createManageBuyOfferOperationResponseMessage(ManageBuyOfferOperationResponse operation) {
    String amount = operation.getAmount();
    String buyingAsset = getAssetName(operation.getBuyingAsset());
    Long offerId = operation.getOfferId();
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
    String body = String.format("Operation Received - Type: %s, Id: %s, SourceAccount: %s", operation.getType(), operation.getId(),
      operation.getSourceAccount());

    return new Message(subject, body);
  }

  private Message createSetOptionsOperationMessage(SetOptionsOperationResponse setOptionsOperation) {
    List<Integer> clearFlags = setOptionsOperation.getClearFlags();
    Integer highThreshold = setOptionsOperation.getHighThreshold();
    String homeDomain = setOptionsOperation.getHomeDomain();
    String inflationDestination = setOptionsOperation.getInflationDestination();
    Integer lowThreshold = setOptionsOperation.getLowThreshold();
    Integer masterKeyWeight = setOptionsOperation.getMasterKeyWeight();
    Integer medThreshold = setOptionsOperation.getMedThreshold();
    List<Integer> setFlags = setOptionsOperation.getSetFlags();
    String signer = setOptionsOperation.getSignerKey();
    Integer signerWeight = setOptionsOperation.getSignerWeight();

    String subject = "Stellar Set Options operation";
    String body = String.format("Set options. clearFlags: %s, highThreshold: %s, homeDomain: %s, inflationDestination: %s, " +
        "lowThreshold: %s, masterKeyWeight: %s, medThreshold: %s, setFlags: %s, signer: %s, signerWeight: %s.",
      StringUtils.join(clearFlags, ","), highThreshold, homeDomain, inflationDestination, lowThreshold, masterKeyWeight, medThreshold,
      StringUtils.join(setFlags, ","), signer, signerWeight);

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
    Long offerId = manageSellOfferOperation.getOfferId();
    String price = manageSellOfferOperation.getPrice();
    String sellingAsset = getAssetName(manageSellOfferOperation.getSellingAsset());

    String body = String.format("Managed sell offer buy %s %s at %s using %s with id %s", amount, buyingAsset, price, sellingAsset,
      offerId);
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
    Long offerId = createPassiveSellOfferOperation.getOfferId();

    String body = String.format("Created passive sell offer buy %s %s at %s using %s with id %s", amount, buyingAsset, price, sellingAsset,
      offerId);
    String subject = "Stellar passive sell offer created.";

    return new Message(subject, body);

  }

  private Message createChangeTrustOperationMessage(ChangeTrustOperationResponse changeTrustOperation) {
    String asset = changeTrustOperation.getAssetCode();
    String trustee = changeTrustOperation.getTrustee();
    String trustor = changeTrustOperation.getTrustor();
    String limit = changeTrustOperation.getLimit();

    String body = String.format("Changed trust, from %s, to %s on %s with limit %s", trustor, trustee, asset, limit);
    String subject = "Stellar change trust";

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
    String memoText = getMemo(paymentOperation);
    Date date = Date.from(Instant.parse(paymentOperation.getCreatedAt()));
    String subject = "Stellar payment operation.";
    String body;
    if (paymentOperation.getTo().equals(accountId)) {
      body = String.format("Received payment of %s %s from %s to %s on %tc.%n Memo: %s", amount, asset, from, to, date, memoText);
    } else {
      body = String.format("Sent payment of %s %s from %s to %s on %tc.%n Memo: %s", amount, asset, from, to, date, memoText);
    }

    if (excludedTokens.contains(getAssetCode(paymentOperation.getAsset()))) {
      return null;
    }

    return new Message(subject, body);
  }

  private String getMemo(PaymentOperationResponse paymentOperation) {
    Memo memo = paymentOperation.getTransaction().getMemo();
    String memoText = "";
    if (memo instanceof MemoText memoT) {
      memoText = memoT.getText();
    }

    return memoText;
  }

  private String getAssetName(Asset asset) {
    String assetName;
    if (asset instanceof AssetTypeNative) {
      assetName = "lumens";
    } else if (asset instanceof AssetTypeCreditAlphaNum pathPaymentBaseOperationResponse) {
      assetName = pathPaymentBaseOperationResponse.getCode();
      assetName += ":";
      assetName += pathPaymentBaseOperationResponse.getIssuer();
    } else {
      assetName = "unknown";
    }

    return assetName;
  }

  private String getAssetCode(Asset asset) {
    String assetCode;
    if (asset instanceof AssetTypeNative) {
      assetCode = "XLM";
    } else if (asset instanceof AssetTypeCreditAlphaNum pathPaymentBaseOperationResponse) {
      assetCode = pathPaymentBaseOperationResponse.getCode();
    } else {
      assetCode = "unknown";
    }

    return assetCode;
  }
}
