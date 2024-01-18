package cn.mulanbay.pms.persistent.dto.auth;

public class FastMenuDTO {

    private Long id;

    private Long menuId;

    private String menuName;

    private String path;

    public FastMenuDTO(Long id, Long menuId, String menuName, String path) {
        this.id = id;
        this.menuId = menuId;
        this.menuName = menuName;
        this.path = path;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
