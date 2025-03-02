package com.dillian.e_mngt_backendforfrontend;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.BuildingRequestDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.ExtendedGameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.MinimizedGameDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameDTOMapper {

    @Mapping(target = "buildingRequests", source = "buildings")
    MinimizedGameDTO toMinimizedGameDTO(ExtendedGameDTO extendedGameDTO);

    default BuildingRequestDTO toBuildingRequestDTO(BuildingDTO buildingDTO) {
        return new BuildingRequestDTO(buildingDTO.getId(), buildingDTO.getSolarPanelAmount());
    }
}
