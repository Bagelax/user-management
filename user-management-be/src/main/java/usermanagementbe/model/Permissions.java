package usermanagementbe.model;

public enum Permissions {
    // User permissions
    CAN_READ_USERS,
    CAN_CREATE_USERS,
    CAN_UPDATE_USERS,
    CAN_DELETE_USERS,

    // Machine permissions
    CAN_SEARCH_MACHINES,
    CAN_START_MACHINES,
    CAN_STOP_MACHINES,
    CAN_RESTART_MACHINES,
    CAN_CREATE_MACHINES,
    CAN_DESTROY_MACHINES
}
