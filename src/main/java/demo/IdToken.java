package demo;

import com.google.gson.annotations.SerializedName;

public class IdToken {
  private String iss;
  private String sub;
  private String aud;
  private long exp;
  private long iat;
  private long auth_time;
  private String nonce;
  private String at_hash;
  @SerializedName("https://slack.com/team_id")  private String slackTeamId;
  @SerializedName("https://slack.com/user_id") private String slackUserId;
  private String locale;
  private String name;
  private String given_name;
  private String family_name;
  @SerializedName("https://slack.com/team_name") private String slackTeamName;
  @SerializedName("https://slack.com/team_domain") private String slackTeamDomain;
  @SerializedName("https://slack.com/team_image_230") private String slackTeamImage;

  @Override
  public String toString() {
    return "IdToken{" +
        "iss='" + iss + '\'' +
        ", sub='" + sub + '\'' +
        ", aud='" + aud + '\'' +
        ", exp=" + exp +
        ", iat=" + iat +
        ", auth_time=" + auth_time +
        ", nonce='" + nonce + '\'' +
        ", at_hash='" + at_hash + '\'' +
        ", slackTeamId='" + slackTeamId + '\'' +
        ", slackUserId='" + slackUserId + '\'' +
        ", locale='" + locale + '\'' +
        ", name='" + name + '\'' +
        ", given_name='" + given_name + '\'' +
        ", family_name='" + family_name + '\'' +
        ", slackTeamName='" + slackTeamName + '\'' +
        ", slackTeamDomain='" + slackTeamDomain + '\'' +
        ", slackTeamImage='" + slackTeamImage + '\'' +
        '}';
  }

  public String getIss() {
    return iss;
  }

  public String getSub() {
    return sub;
  }

  public String getAud() {
    return aud;
  }

  public long getExp() {
    return exp;
  }

  public long getIat() {
    return iat;
  }

  public long getAuth_time() {
    return auth_time;
  }

  public String getNonce() {
    return nonce;
  }

  public String getAt_hash() {
    return at_hash;
  }

  public String getSlackTeamId() {
    return slackTeamId;
  }

  public String getSlackUserId() {
    return slackUserId;
  }

  public String getLocale() {
    return locale;
  }

  public String getName() {
    return name;
  }

  public String getGiven_name() {
    return given_name;
  }

  public String getFamily_name() {
    return family_name;
  }

  public String getSlackTeamName() {
    return slackTeamName;
  }

  public String getSlackTeamDomain() {
    return slackTeamDomain;
  }

  public String getSlackTeamImage() {
    return slackTeamImage;
  }
}
