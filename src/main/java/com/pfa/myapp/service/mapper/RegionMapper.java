package com.pfa.myapp.service.mapper;

import com.pfa.myapp.domain.Region;
import com.pfa.myapp.service.dto.RegionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Region} and its DTO {@link RegionDTO}.
 */
@Mapper(componentModel = "spring")
public interface RegionMapper extends EntityMapper<RegionDTO, Region> {}
