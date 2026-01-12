package model.bean;

import java.util.List;

public class Menu {

    private int menuID;
    private String menuName;
    private boolean isActive;
    private String controllerName;
    private String actionName;
    private int levels;
    private int parentID;
    private int menuOrder;
    private int position;
    private String icon;
    private String idName;
    private String itemTarget;

    private List<Menu> subMenus;

    public int getMenuID() { return menuID; }
    public void setMenuID(int menuID) { this.menuID = menuID; }

    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getControllerName() { return controllerName; }
    public void setControllerName(String controllerName) { this.controllerName = controllerName; }

    public String getActionName() { return actionName; }
    public void setActionName(String actionName) { this.actionName = actionName; }

    public int getLevels() { return levels; }
    public void setLevels(int levels) { this.levels = levels; }

    public int getParentID() { return parentID; }
    public void setParentID(int parentID) { this.parentID = parentID; }

    public int getMenuOrder() { return menuOrder; }
    public void setMenuOrder(int menuOrder) { this.menuOrder = menuOrder; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getIdName() { return idName; }
    public void setIdName(String idName) { this.idName = idName; }

    public String getItemTarget() { return itemTarget; }
    public void setItemTarget(String itemTarget) { this.itemTarget = itemTarget; }

    public List<Menu> getSubMenus() { return subMenus; }
    public void setSubMenus(List<Menu> subMenus) { this.subMenus = subMenus; }
}
