package org.example.car_management_system.service.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.car_management_system.config.exception.DuplicateManufactureException;
import org.example.car_management_system.config.exception.ManufactureNotFoundException;
import org.example.car_management_system.dto.request.CreateManufactureRequest;
import org.example.car_management_system.dto.response.ManufactoryResponse;
import org.example.car_management_system.dto.response.ResponseData;
import org.example.car_management_system.mapper.ManufacturerMapper;
import org.example.car_management_system.model.Manufacture;
import org.example.car_management_system.model.QManufacture;
import org.example.car_management_system.repository.ManufactureRepository;
import org.example.car_management_system.service.ManufactureService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManufactureServiceImpl implements ManufactureService {

    private final JPAQueryFactory queryFactory;
    private final ManufacturerMapper manufactureMapper;
    private final ManufactureRepository manufactureRepository;

    @Override
    public ResponseData<List<ManufactoryResponse>> getAll(int page, int size) {
        QManufacture manufacture = QManufacture.manufacture;

        List<Manufacture> result = queryFactory.selectFrom(manufacture)
                .offset((long) page * size)
                .limit(size)
                .fetch();

        if (result.isEmpty()) {
            throw new ManufactureNotFoundException("Manufactures not found!");
        }

        List<ManufactoryResponse> responseList = manufactureMapper.toDtoList(result);

        return new ResponseData<>(200, "Manufactures retrieved successfully", responseList);
    }
    @Override
    public ManufactoryResponse create(CreateManufactureRequest request) {

        QManufacture manufacture = QManufacture.manufacture;

        // Kiểm tra name và country có tồn tại chưa
        Manufacture existing = queryFactory.selectFrom(manufacture)
                .where(
                        manufacture.name.eq(request.getName())
                                .and(manufacture.country.eq(request.getCountry()))
                )
                .fetchOne();

        if (existing != null) {
            throw new DuplicateManufactureException("Manufacture with same name and country already exists");
        }

        Manufacture newManufacture = manufactureMapper.toEntity(request);

        Manufacture saved = manufactureRepository.save(newManufacture);

        return manufactureMapper.toDto(saved);
    }

}
