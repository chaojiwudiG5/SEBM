package group5.sebm.audit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.BorrowRecord.dao.BorrowRecordMapper;
import group5.sebm.BorrowRecord.entity.BorrowRecordPo;
import group5.sebm.Device.entity.DevicePo;
import group5.sebm.Device.service.services.DeviceService;
import group5.sebm.Maintenance.dao.MechanicanMaintenanceRecordMapper;
import group5.sebm.Maintenance.dao.UserMaintenanceRecordMapper;
import group5.sebm.Maintenance.entity.MechanicanMaintenanceRecordPo;
import group5.sebm.Maintenance.entity.UserMaintenanceRecordPo;
import group5.sebm.User.dao.UserMapper;
import group5.sebm.User.entity.UserPo;
import group5.sebm.audit.dto.*;
import group5.sebm.audit.service.AuditService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuditServiceImpl implements AuditService {

    private final BorrowRecordMapper borrowRecordMapper;
    private final DeviceService deviceService;
    private final UserMapper userMapper;
    private final UserMaintenanceRecordMapper userMaintenanceRecordMapper;
    private final MechanicanMaintenanceRecordMapper mechanicanMaintenanceRecordMapper;

    public AuditServiceImpl(BorrowRecordMapper borrowRecordMapper,
                            DeviceService deviceService,
                            UserMapper userMapper,
                            UserMaintenanceRecordMapper userMaintenanceRecordMapper,
                            MechanicanMaintenanceRecordMapper mechanicanMaintenanceRecordMapper) {
        this.borrowRecordMapper = borrowRecordMapper;
        this.deviceService = deviceService;
        this.userMapper = userMapper;
        this.userMaintenanceRecordMapper = userMaintenanceRecordMapper;
        this.mechanicanMaintenanceRecordMapper = mechanicanMaintenanceRecordMapper;
    }

    @Override
    public OverviewDto getOverview() {
        OverviewDto d = new OverviewDto();
        // total devices
        d.totalDevices = (int) deviceService.count();
        // currently borrowed (returnTime is null)
        Long currentlyBorrowed = borrowRecordMapper.selectCount(new QueryWrapper<BorrowRecordPo>().isNull("returnTime"));
        d.currentlyBorrowed = currentlyBorrowed == null ? 0 : currentlyBorrowed.intValue();
        // currently in maintenance (user maintenance records with status = processing (0))
        d.currentlyInMaintenance = userMaintenance_record_count();
        // totals in period are left 0 (require start/end params)
        d.totalBorrowInPeriod = 0;
        d.totalMaintenanceInPeriod = 0;
        d.topBorrowDepartments = Collections.emptyList();
        d.topBorrowUsers = Collections.emptyList();
        return d;
    }

    private Integer userMaintenance_record_count() {
        Long cnt = userMaintenanceRecordMapper.selectCount(new QueryWrapper<UserMaintenanceRecordPo>().eq("status", 0));
        return cnt == null ? 0 : cnt.intValue();
    }

    @Override
    public PagedResponse listDevices(int page, int size) {
        Page<BorrowRecordPo> pr = new Page<>(page, size);
        Page<BorrowRecordPo> result = borrowRecordMapper.selectPage(pr, new QueryWrapper<>());
        List<Object> items = result.getRecords().stream().map(record -> {
            AuditDeviceDto dto = new AuditDeviceDto();
            dto.id = record.getId() == null ? null : String.valueOf(record.getId());
            DevicePo device = record.getDeviceId() == null ? null : deviceService.getById(record.getDeviceId());
            if (device != null) {
                dto.deviceId = device.getId() == null ? null : String.valueOf(device.getId());
                dto.deviceName = device.getDeviceName();
                dto.deviceType = device.getDeviceType();
                dto.department = device.getLocation();
            }
            UserPo user = record.getUserId() == null ? null : userMapper.selectById(record.getUserId());
            dto.user = user == null ? null : user.getUsername();
            dto.borrowTime = record.getBorrowTime() == null ? null : record.getBorrowTime().toInstant().toString();
            dto.expectedReturn = record.getDueTime() == null ? null : record.getDueTime().toInstant().toString();
            dto.returnTime = record.getReturnTime() == null ? null : record.getReturnTime().toInstant().toString();
            dto.status = record.getStatus() == null ? null : String.valueOf(record.getStatus());
            dto.isOverdue = (record.getReturnTime() == null && record.getDueTime() != null && record.getDueTime().before(new Date()));
            if (dto.isOverdue && record.getDueTime() != null) {
                long diff = (new Date().getTime() - record.getDueTime().getTime());
                dto.overdueDays = (int) (diff / (1000L * 60 * 60 * 24));
            } else {
                dto.overdueDays = 0;
            }
            return dto;
        }).collect(Collectors.toList());
        PagedResponse resp = new PagedResponse();
        resp.total = (int) result.getTotal();
        resp.page = (int) result.getCurrent();
        resp.size = (int) result.getSize();
        resp.items = items;
        return resp;
    }

    @Override
    public AuditDeviceDto getDevice(String id) {
        try {
            Long deviceId = Long.parseLong(id);
            DevicePo device = deviceService.getById(deviceId);
            if (device == null) return null;
            AuditDeviceDto dto = new AuditDeviceDto();
            dto.id = device.getId() == null ? null : String.valueOf(device.getId());
            dto.deviceId = dto.id;
            dto.deviceName = device.getDeviceName();
            dto.deviceType = device.getDeviceType();

            BorrowRecordPo record = borrowRecordMapper.selectOne(new QueryWrapper<BorrowRecordPo>().eq("deviceId", deviceId).orderByDesc("borrowTime").last("LIMIT 1"));
            if (record != null) {
                UserPo user = record.getUserId() == null ? null : userMapper.selectById(record.getUserId());
                dto.user = user == null ? null : user.getUsername();
                dto.borrowTime = record.getBorrowTime() == null ? null : record.getBorrowTime().toInstant().toString();
                dto.expectedReturn = record.getDueTime() == null ? null : record.getDueTime().toInstant().toString();
                dto.returnTime = record.getReturnTime() == null ? null : record.getReturnTime().toInstant().toString();
                dto.status = record.getStatus() == null ? null : String.valueOf(record.getStatus());
                dto.isOverdue = (record.getReturnTime() == null && record.getDueTime() != null && record.getDueTime().before(new Date()));
                dto.overdueDays = dto.isOverdue ? (int) ((new Date().getTime() - record.getDueTime().getTime()) / (1000L * 60 * 60 * 24)) : 0;
            }

            List<UserMaintenanceRecordPo> userMaint = userMaintenanceRecordMapper.selectList(new QueryWrapper<UserMaintenanceRecordPo>().eq("deviceId", deviceId));
            List<MechanicanMaintenanceRecordPo> mechMaint = mechanicanMaintenance_record_list(deviceId);
            List<MaintenanceRecordDto> mrecords = new ArrayList<>();
            for (UserMaintenanceRecordPo m : userMaint) {
                MaintenanceRecordDto md = new MaintenanceRecordDto();
                md.id = m.getId() == null ? null : String.valueOf(m.getId());
                md.deviceId = m.getDeviceId() == null ? null : String.valueOf(m.getDeviceId());
                md.start = m.getCreateTime() == null ? null : m.getCreateTime().toInstant().toString();
                md.end = m.getUpdateTime() == null ? null : m.getUpdateTime().toInstant().toString();
                md.durationMinutes = null;
                md.reason = m.getDescription();
                md.technician = null;
                mrecords.add(md);
            }
            for (MechanicanMaintenanceRecordPo m : mechMaint) {
                MaintenanceRecordDto md = new MaintenanceRecordDto();
                md.id = m.getId() == null ? null : String.valueOf(m.getId());
                md.deviceId = m.getDeviceId() == null ? null : String.valueOf(m.getDeviceId());
                md.start = m.getCreateTime() == null ? null : m.getCreateTime().toInstant().toString();
                md.end = m.getUpdateTime() == null ? null : m.getUpdateTime().toInstant().toString();
                md.durationMinutes = null;
                md.reason = m.getDescription();
                md.technician = null;
                mrecords.add(md);
            }
            dto.maintenanceRecords = mrecords;
            return dto;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private List<MechanicanMaintenanceRecordPo> mechanicanMaintenance_record_list(Long deviceId) {
        return mechanicanMaintenanceRecordMapper.selectList(new QueryWrapper<MechanicanMaintenanceRecordPo>().eq("deviceId", deviceId));
    }

    @Override
    public Map<String, Object> submitBorrowRequest(BorrowRequestDto request) {
        Map<String, Object> r = new HashMap<>();
        BorrowRecordPo po = new BorrowRecordPo();
        if (request.deviceId != null) {
            try {
                po.setDeviceId(Long.parseLong(request.deviceId));
            } catch (NumberFormatException ignored) {
            }
        }
        UserPo user = null;
        if (request.user != null) {
            user = userMapper.selectOne(new QueryWrapper<UserPo>().eq("username", request.user));
        }
        if (user != null) po.setUserId(user.getId());
        borrowRecordMapper.insert(po);
        r.put("success", true);
        r.put("requestId", po.getId());
        return r;
    }

    @Override
    public byte[] exportDevices(String format) {
        List<BorrowRecordPo> list = borrowRecordMapper.selectList(new QueryWrapper<>());
        StringBuilder sb = new StringBuilder();
        sb.append("id,deviceId,deviceName,deviceType,user,borrowTime,dueTime,returnTime,status,overdueDays\n");
        for (BorrowRecordPo rec : list) {
            DevicePo d = rec.getDeviceId() == null ? null : deviceService.getById(rec.getDeviceId());
            UserPo u = rec.getUserId() == null ? null : userMapper.selectById(rec.getUserId());
            sb.append(rec.getId()).append(",")
                .append(rec.getDeviceId()).append(",")
                .append(d == null ? "" : d.getDeviceName()).append(",")
                .append(d == null ? "" : d.getDeviceType()).append(",")
                .append(u == null ? "" : u.getUsername()).append(",")
                .append(rec.getBorrowTime()).append(",")
                .append(rec.getDueTime()).append(",")
                .append(rec.getReturnTime()).append(",")
                .append(rec.getStatus()).append(",")
                .append("0").append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public PagedResponse listMaintenance(int page, int size) {
        Page<UserMaintenanceRecordPo> p = new Page<>(page, size);
        Page<UserMaintenanceRecordPo> result = userMaintenanceRecordMapper.selectPage(p, new QueryWrapper<>());
        List<Object> items = result.getRecords().stream().map(m -> {
            MaintenanceRecordDto md = new MaintenanceRecordDto();
            md.id = m.getId() == null ? null : String.valueOf(m.getId());
            md.deviceId = m.getDeviceId() == null ? null : String.valueOf(m.getDeviceId());
            md.start = m.getCreateTime() == null ? null : m.getCreateTime().toInstant().toString();
            md.end = m.getUpdateTime() == null ? null : m.getUpdateTime().toInstant().toString();
            md.durationMinutes = null;
            md.reason = m.getDescription();
            md.technician = null;
            return md;
        }).collect(Collectors.toList());
        PagedResponse resp = new PagedResponse();
        resp.total = (int) result.getTotal();
        resp.page = (int) result.getCurrent();
        resp.size = (int) result.getSize();
        resp.items = items;
        return resp;
    }

    @Override
    public List<MaintenanceStatsItemDto> getMaintenanceStats(String start, String end, String groupBy) {
        // Fetch all maintenance records (user + mechanic)
        List<UserMaintenanceRecordPo> userMaint = userMaintenanceRecordMapper.selectList(new QueryWrapper<>());
        List<MechanicanMaintenanceRecordPo> mechMaint = mechanicanMaintenanceRecordMapper.selectList(new QueryWrapper<>());

        // Map deviceType -> [totalRecords, totalMinutesSum, completedCount]
        Map<String, long[]> agg = new HashMap<>();

        // helper to add record
        java.util.function.BiConsumer<String, Long> addDuration = (deviceType, minutes) -> {
            long[] a = agg.computeIfAbsent(deviceType == null ? "unknown" : deviceType, k -> new long[3]);
            a[1] += (minutes == null ? 0L : minutes);
            if (minutes != null) a[2] += 1;
        };

        // process user maintenance records
        for (UserMaintenanceRecordPo m : userMaint) {
            String deviceType = "unknown";
            if (m.getDeviceId() != null) {
                DevicePo d = deviceService.getById(m.getDeviceId());
                if (d != null && d.getDeviceType() != null) deviceType = d.getDeviceType();
            }
            long[] a = agg.computeIfAbsent(deviceType, k -> new long[3]);
            a[0] += 1; // total records
            if (m.getCreateTime() != null && m.getUpdateTime() != null) {
                long diffMs = m.getUpdateTime().getTime() - m.getCreateTime().getTime();
                if (diffMs > 0) {
                    long minutes = diffMs / 60000L;
                    a[1] += minutes;
                    a[2] += 1; // completed count
                }
            }
        }

        // process mechanic maintenance records
        for (MechanicanMaintenanceRecordPo m : mechMaint) {
            String deviceType = "unknown";
            if (m.getDeviceId() != null) {
                DevicePo d = deviceService.getById(m.getDeviceId());
                if (d != null && d.getDeviceType() != null) deviceType = d.getDeviceType();
            }
            long[] a = agg.computeIfAbsent(deviceType, k -> new long[3]);
            a[0] += 1; // total records
            if (m.getCreateTime() != null && m.getUpdateTime() != null) {
                long diffMs = m.getUpdateTime().getTime() - m.getCreateTime().getTime();
                if (diffMs > 0) {
                    long minutes = diffMs / 60000L;
                    a[1] += minutes;
                    a[2] += 1;
                }
            }
        }

        // build DTOs
        List<MaintenanceStatsItemDto> stats = new ArrayList<>();
        for (Map.Entry<String, long[]> e : agg.entrySet()) {
            String deviceType = e.getKey();
            long[] a = e.getValue();
            MaintenanceStatsItemDto it = new MaintenanceStatsItemDto();
            it.deviceType = deviceType;
            it.maintenanceCount = (int) a[0];
            if (a[2] > 0) {
                it.avgMaintenanceMinutes = a[1] / (double) a[2];
            } else {
                it.avgMaintenanceMinutes = null;
            }
            stats.add(it);
        }

        // sort by maintenanceCount desc
        stats.sort((x, y) -> Integer.compare(y.maintenanceCount == null ? 0 : y.maintenanceCount, x.maintenanceCount == null ? 0 : x.maintenanceCount));
        return stats;
    }

    @Override
    public List<BorrowStatsItemDto> getBorrowStats(String start, String end, String groupBy, int top) {
        return Collections.emptyList();
    }

    @Override
    public List<PersonnelTopItemDto> getPersonnelTop(String start, String end, int top) {
        List<BorrowRecordPo> all = borrowRecordMapper.selectList(new QueryWrapper<>());
        Map<Long, Long> counts = all.stream().collect(Collectors.groupingBy(BorrowRecordPo::getUserId, Collectors.counting()));
        List<Map.Entry<Long, Long>> sorted = counts.entrySet().stream().sorted((a, b) -> Long.compare(b.getValue(), a.getValue())).limit(top).collect(Collectors.toList());
        List<PersonnelTopItemDto> res = new ArrayList<>();
        for (Map.Entry<Long, Long> e : sorted) {
            UserPo u = e.getKey() == null ? null : userMapper.selectById(e.getKey());
            PersonnelTopItemDto it = new PersonnelTopItemDto();
            it.user = u == null ? String.valueOf(e.getKey()) : u.getUsername();
            it.borrowCount = e.getValue().intValue();
            it.overdueCount = 0;
            it.department = null;
            it.lastBorrow = null;
            res.add(it);
        }
        return res;
    }

    @Override
    public ExportJobDto createExportJob(Map<String, Object> body) {
        return new ExportJobDto(UUID.randomUUID().toString(), "created", null);
    }

    @Override
    public ExportJobDto getExportJobStatus(String id) {
        return new ExportJobDto(id, "completed", "/api/audit/devices/export?format=csv&jobId=" + id);
    }
}
