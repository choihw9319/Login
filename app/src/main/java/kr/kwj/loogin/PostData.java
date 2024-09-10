package kr.kwj.loogin;

public class PostData {
    private String email;
    private String id;
    private String residence;
    private String destination;

    public PostData(String email, String id, String residence, String destination) {
        this.email = email;
        this.id = id;
        this.residence = residence;
        this.destination = destination;
    }

    // Getter와 Setter 추가
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
