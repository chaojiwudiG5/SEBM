# Maintenance Backend Module Plan

This document describes how to extend the current Spring Boot + MyBatis-Plus codebase to support the end-to-end device maintenance workflow (user submit → admin assign → mechanican repair → admin confirm).

## 1. Reuse the existing layering conventions

The existing `group5.sebm.User` module separates responsibilities into `controller`, `service`, `dao`, `entity`, `enums`, and `controller/dto` / `controller/vo` packages, and relies on:

- `BaseResponse` / `ResultUtils` for unified response envelopes.
- `ServiceImpl` from MyBatis-Plus as the base class for service implementations.
- `@AuthCheck` AOP to enforce role checks in the controller layer (`AuthInterceptor`).

Follow the same conventions for the maintenance feature by creating a new top-level package `group5.sebm.maintenance` with the same sub-package layout:

```
group5/sebm/maintenance
├── controller
│   ├── AdminMaintenanceController.java
│   ├── MechanicanMaintenanceController.java
│   ├── UserMaintenanceController.java
│   ├── dto
│   │   ├── AssignRequest.java
│   │   ├── SubmitMaintenanceRequest.java
│   │   ├── SubmitRepairRequest.java
│   │   └── VerifyMaintenanceRequest.java
│   └── vo
│       └── MaintenanceRecordVo.java
├── service
│   ├── AdminMaintenanceService.java
│   ├── MechanicanMaintenanceService.java
│   ├── UserMaintenanceService.java
│   ├── impl (if you prefer to keep implementations separate)
│   └── bo / converter (optional helpers)
├── dao
│   ├── DeviceMapper.java
│   ├── UserMaintenanceRecordMapper.java
│   └── MechanicanMaintenanceRecordMapper.java
├── entity
│   ├── DevicePo.java
│   ├── UserMaintenanceRecordPo.java
│   └── MechanicanMaintenanceRecordPo.java
└── enums
    ├── DeviceStatusEnum.java
    └── MaintenanceRecordStatusEnum.java
```

Add mapper XML files under `src/main/resources/mapper/maintenance/` mirroring the DAO interfaces whenever custom SQL is required.

## 2. Database mappings and domain entities

Create MyBatis-Plus `@TableName` entity classes matching the schema shared in the specification:

- `DevicePo` → `device`
- `UserMaintenanceRecordPo` → `userMaintenanceRecord`
- `MechanicanMaintenanceRecordPo` → `mechanicanMaintenanceRecord`

Include the relevant fields (e.g., `status`, `description`, `image`, `createTime`, `updateTime`, `isDelete`) and enable `@TableLogic` for soft-delete flags where applicable, consistent with `UserPo`.

Define enums to replace "magic numbers":

- `DeviceStatusEnum` with values such as `AVAILABLE(1)`, `MAINTAINING(2)`, `RESERVED(3)` (extend with other statuses already present in the system).
- `MaintenanceRecordStatusEnum` for `PENDING`, `ASSIGNED`, `IN_PROGRESS`, `COMPLETED`, `REJECTED`, etc., so that both user and mechanican records can store workflow state transitions.

Expose helper methods inside the enums to translate between database integers and business-friendly values, similar to `UserRoleEnum`.

## 3. Data transfer objects and view models

Create request/response models inside `controller/dto` and `controller/vo` packages to decouple API payloads from persistence objects:

- `SubmitMaintenanceRequest` → used by end users. Contains `deviceId`, `description`, `imageUrl`, optional contact info.
- `AssignRequest` → used by admins. Contains `maintenanceRecordId`, `mechanicanId`, `dueDate` (if needed).
- `SubmitRepairRequest` → used by mechanicans. Contains `mechanicanRecordId`, `description`, `imageUrl`, optional cost/time info.
- `VerifyMaintenanceRequest` → used by admins to confirm the repair, including final comments and pass/fail flag.
- `MaintenanceRecordVo` → aggregated view combining device info, user submission, assigned mechanican, and current status for list/detail endpoints.

Use converters (static methods or MapStruct) if you want to avoid manual `BeanUtils` copying, just like `UserServiceImpl` currently does.

## 4. Service layer responsibilities

1. **UserMaintenanceService**
   - `submitRequest(SubmitMaintenanceRequest dto, Long userId)`
     1. Validate device existence and availability (`DeviceMapper.selectById`).
     2. Insert a `UserMaintenanceRecordPo` with status `PENDING`.
     3. Update `device.status` to `MAINTAINING` and stamp `updateTime`.
     4. Optionally create a `notificationRecord` for admins.
     5. Wrap the above in `@Transactional` to keep DB updates consistent.

   - `listOwnRequests(Long userId, PageRequest)` to let users see submission history.

2. **AdminMaintenanceService**
   - `listPendingRequests(PageRequest)` to fetch user-submitted records waiting for assignment.
   - `assignMechanican(AssignRequest dto, Long adminId)`
     1. Validate admin role via `@AuthCheck(mustRole = "admin")` in the controller.
     2. Verify the target mechanican has the proper role (requires extending `UserRoleEnum` and storing mechanic role in the `user` table).
     3. Create or update a `MechanicanMaintenanceRecordPo` linked to the user record.
     4. Update `UserMaintenanceRecord.status` to `ASSIGNED`.
     5. Send notification to the mechanican (insert into `notificationRecord`).

   - `verifyRepair(VerifyMaintenanceRequest dto, Long adminId)`
     1. Validate mechanican record exists and is completed.
     2. Set user record status to `COMPLETED` or `REJECTED`.
     3. Update `device.status` to `AVAILABLE` (or another state if rejected) and log an `operationLog` entry if the project uses that table.

3. **MechanicanMaintenanceService**
   - `listAssignedJobs(Long mechanicanId, PageRequest)`.
   - `submitRepairReport(SubmitRepairRequest dto, Long mechanicanId)`
     1. Validate ownership of the assignment.
     2. Persist description/image to `MechanicanMaintenanceRecord`.
     3. Update status to `IN_PROGRESS` / `COMPLETED` as appropriate.
     4. Notify admins for verification.

Each service should extend `ServiceImpl<Mapper, Po>` to reuse CRUD helpers, mirroring `UserServiceImpl`. Declare interfaces when multiple implementations are expected, or follow the current approach (e.g., `BorrowerServiceImpl` directly implements `UserService`).

## 5. Controller layer endpoints

Create dedicated controllers under `/maintenance` with RESTful endpoints:

- `/maintenance/user` (requires authenticated user)
  - `POST /request` → `submitRequest`
  - `GET /records` → `listOwnRequests`

- `/maintenance/admin` (guarded by `@AuthCheck(mustRole = "admin")`)
  - `GET /pending` → view unassigned requests
  - `POST /assign` → assign mechanican
  - `POST /verify` → finalize record

- `/maintenance/mechanican` (guarded by a new `@AuthCheck(mustRole = "mechanican")` once `UserRoleEnum` is extended)
  - `GET /assigned` → fetch assigned jobs
  - `POST /report` → upload repair outcome

Reuse `ResultUtils.success(...)` to wrap responses, matching the pattern in `UserController`.

## 6. Cross-cutting concerns

- **Role management**: Extend `UserRoleEnum` and the `AuthInterceptor` so that `mechanican` becomes a first-class role. Update the `user` table seed data or migration logic accordingly.
- **Validation**: Apply `@Valid` annotations on DTOs and add custom validators for uploaded images/description length.
- **Transactions**: Use `@Transactional` on service methods that touch multiple tables (e.g., user submission, assignment, verification) to keep statuses consistent.
- **Notifications**: Implement helper methods (or a `NotificationService`) to insert rows into `notificationRecord` and reuse existing templates from `notificationTemplate` if applicable.
- **Operation logs**: If `operationLog` is already used elsewhere, extend it to track assignment/verification events for auditing.

## 7. Testing strategy

- Create integration tests under `src/test/java/group5/sebm/maintenance` to cover the happy path scenario (user submit → admin assign → mechanican report → admin verify) using an in-memory database or transactional rollbacks.
- Add mapper tests to ensure custom queries join the correct tables.
- Mock notification service if it triggers external side effects.

## 8. Migration steps

1. Introduce the new packages, entities, and mappers.
2. Extend `UserRoleEnum` + `AuthInterceptor` to recognise the mechanican role.
3. Implement service logic with transactions and ensure device status transitions are consistent with the enumerations.
4. Wire controllers and expose endpoints via Swagger/OpenAPI annotations (`@Tag`), similar to existing controllers.
5. Add tests and, if required, database migration scripts (e.g., Flyway) to populate mechanic users.

Following this plan keeps the new maintenance workflow consistent with the existing project structure and coding standards while covering all role-specific responsibilities.
