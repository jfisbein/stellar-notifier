package com.sputnik.stellar;

import com.sputnik.stellar.util.ConfigManager;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.stellar.sdk.Asset;
import org.stellar.sdk.Claimant;
import org.stellar.sdk.Predicate;
import org.stellar.sdk.Server;
import org.stellar.sdk.requests.RequestBuilder.Order;
import org.stellar.sdk.responses.ClaimableBalanceResponse;
import org.stellar.sdk.responses.Page;
import org.stellar.sdk.xdr.AssetType;

public class ClaimableLauncher {

  private static final ConfigManager config = new ConfigManager(new File(System.getProperty("user.home"), ".stellar-notifier"));
  private static final Set<String> assetsBlockList = Set.of("trumpcoin", "TRUTHSOCIAL");

  public static void main(String[] args) {
    new ClaimableLauncher().launch2();
  }

  private void launch() {
    String monitoredAccountId = config.get("AccountId");
    try (Server server = new Server("https://horizon.stellar.org")) {
      Page<ClaimableBalanceResponse> claimableBalanceResponsePage = server.claimableBalances().order(Order.DESC)
        .forClaimant(monitoredAccountId).limit(50)
        .execute();
      claimableBalanceResponsePage.getRecords().forEach(claimableBalanceResponse -> {

        List<String> claimableBalances = claimableBalanceResponse.getClaimants().stream()
          .filter(claimant -> claimant.getDestination().equalsIgnoreCase(monitoredAccountId))
          .filter(claimant -> !assetsBlockList.contains(getAssetCode(claimableBalanceResponse.getAsset())))
          .filter(claimant -> evaluatePredicate(claimant.getPredicate(), Instant.now()))
          .map(claimant -> " Claimable Balance " +
            claimableBalanceResponse.getAmount() + " " + getAssetCode(claimableBalanceResponse.getAsset()) + " " + predicateToText(
            claimant.getPredicate()) + " " + evaluatePredicate(claimant.getPredicate(), Instant.now()))
          .toList();

        System.out.println(claimableBalances.stream().collect(Collectors.joining("\n")));
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void launch2() {
    String monitoredAccountId = config.get("AccountId");
    try (Server server = new Server("https://horizon.stellar.org")) {
      getActualClaimableBalanceResponse2(monitoredAccountId, server).forEach(
        claim -> System.out.println(predicateToText(claim.getPredicate())));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private List<Claimant> getActualClaimableBalanceResponse2(String accountId, Server server) throws IOException {
    return server.claimableBalances().order(Order.DESC)
      .forClaimant(accountId).limit(50)
      .execute().getRecords().stream()
      .filter(claimableBalanceResponse -> !assetsBlockList.contains(getAssetCode(claimableBalanceResponse.getAsset())))
      .flatMap(claimableBalanceResponse -> claimableBalanceResponse.getClaimants().stream())
      .filter(claimant -> claimant.getDestination().equalsIgnoreCase(accountId))
      .filter(claimant -> evaluatePredicate(claimant.getPredicate(), Instant.now().plus(15, ChronoUnit.DAYS)))
      .toList();
  }


  public String predicateToText(Predicate predicate) {
    if (predicate instanceof Predicate.Unconditional) {
      return "Unconditional";
    } else if (predicate instanceof Predicate.And predicateAnd) {
      return predicateAnd.getInner().stream().map(this::predicateToText).collect(Collectors.joining(" and "));
    } else if (predicate instanceof Predicate.Or predicateOr) {
      return predicateOr.getInner().stream().map(this::predicateToText).collect(Collectors.joining(" or "));
    } else if (predicate instanceof Predicate.Not predicateNot) {
      return "not " + predicateToText(predicateNot.getInner());
    } else if (predicate instanceof Predicate.AbsBefore predicateAbsBefore) {
      return "Before " + predicateAbsBefore.getDate();
    } else if (predicate instanceof Predicate.RelBefore predicateRelBefore) {
      return "Before " + predicateRelBefore.getSecondsSinceClose();
    } else {
      return "Unknown";
    }
  }

  public boolean evaluatePredicate(Predicate predicate, Instant now) {
    if (predicate instanceof Predicate.Unconditional) {
      return true;
    } else if (predicate instanceof Predicate.And predicateAnd) {
      return predicateAnd.getInner().stream().allMatch(p -> evaluatePredicate(p, now));
    } else if (predicate instanceof Predicate.Or predicateOr) {
      return predicateOr.getInner().stream().anyMatch(p -> evaluatePredicate(p, now));
    } else if (predicate instanceof Predicate.Not predicateNot) {
      return !evaluatePredicate(predicateNot.getInner(), now);
    } else if (predicate instanceof Predicate.AbsBefore predicateAbsBefore) {
      return now.isBefore(predicateAbsBefore.getDate());
    } else if (predicate instanceof Predicate.RelBefore predicateRelBefore) {
      return predicateRelBefore.getSecondsSinceClose() > 0;
    } else {
      return false;
    }
  }

  private String getAssetCode(Asset asset) {
    AssetType type = asset.toXdr().getDiscriminant();
    if (type == AssetType.ASSET_TYPE_NATIVE) {
      return "XLM";
    } else if (type == AssetType.ASSET_TYPE_CREDIT_ALPHANUM4) {
      return new String(asset.toXdr().getAlphaNum4().getAssetCode().getAssetCode4()).trim();
    } else if (type == AssetType.ASSET_TYPE_CREDIT_ALPHANUM12) {
      return new String(asset.toXdr().getAlphaNum12().getAssetCode().getAssetCode12()).trim();
    } else {
      return "Unknown asset type";
    }
  }
}
