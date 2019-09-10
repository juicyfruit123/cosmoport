package com.space.service;

import com.space.controller.ShipOrder;
import com.space.exceptions.BadRequestException;
import com.space.exceptions.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipService {

    @Autowired
    ShipRepository shipRepository;

    public Comparator<Ship> getComparatorByOrder(String order) {
        Comparator<Ship> comparator = null;
        switch (ShipOrder.valueOf(order)) {
            case ID: {
                comparator = Comparator.comparing(Ship::getId);
                break;
            }
            case SPEED: {
                comparator = Comparator.comparing(Ship::getSpeed);
                break;
            }
            case DATE: {
                comparator = Comparator.comparing(Ship::getProdDate);
                break;
            }
            case RATING: {
                comparator = Comparator.comparing(Ship::getRating);
            }
        }
        return comparator;
    }

    public List<Ship> getFilteredShipsList(String name,
                                           String planet,
                                           String shipType,
                                           Long after,
                                           Long before,
                                           Boolean isUsed,
                                           Double minSpeed,
                                           Double maxSpeed,
                                           Integer minCrewSize,
                                           Integer maxCrewSize,
                                           Double minRating,
                                           Double maxRating) {

        List<Ship> filteredShipsList = shipRepository.findAll();

        if (name != null) {
            filteredShipsList = filteredShipsList.stream()
                    .filter(ship -> ship.getName().toLowerCase()
                            .contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (planet != null) {
            filteredShipsList = filteredShipsList.stream()
                    .filter(ship -> ship.getPlanet().toLowerCase()
                            .contains(planet.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (shipType != null) {
            filteredShipsList = filteredShipsList.stream()
                    .filter(ship -> ship.getShipType().equals(ShipType.valueOf(shipType)))
                    .collect(Collectors.toList());
        }

        if (after != null) {
            filteredShipsList = filteredShipsList.stream()
                    .filter(ship -> ship.getProdDate().after(new Date(after)))
                    .collect(Collectors.toList());
        }

        if (before != null) {
            filteredShipsList = filteredShipsList.stream()
                    .filter(ship -> ship.getProdDate().before(new Date(before)))
                    .collect(Collectors.toList());
        }

        if (isUsed != null) {
            filteredShipsList = filteredShipsList.stream()
                    .filter(ship -> ship.isUsed().equals(isUsed))
                    .collect(Collectors.toList());
        }

        if (minSpeed != null) {
            filteredShipsList = filteredShipsList.stream()
                    .filter(ship -> ship.getSpeed() >= minSpeed)
                    .collect(Collectors.toList());
        }

        if (maxSpeed != null) {
            filteredShipsList = filteredShipsList.stream()
                    .filter(ship -> ship.getSpeed() <= maxSpeed)
                    .collect(Collectors.toList());
        }

        if (minCrewSize != null) {
            filteredShipsList = filteredShipsList.stream()
                    .filter(ship -> ship.getCrewSize() >= minCrewSize)
                    .collect(Collectors.toList());
        }

        if (maxCrewSize != null) {
            filteredShipsList = filteredShipsList.stream()
                    .filter(ship -> ship.getCrewSize() <= maxCrewSize)
                    .collect(Collectors.toList());
        }

        if (minRating != null) {
            filteredShipsList = filteredShipsList.stream()
                    .filter(ship -> ship.getRating() >= minRating)
                    .collect(Collectors.toList());
        }

        if (maxRating != null) {
            filteredShipsList = filteredShipsList.stream()
                    .filter(ship -> ship.getRating() <= maxRating)
                    .collect(Collectors.toList());
        }

        return filteredShipsList;
    }


    public List<Ship> getSortedShipsList(List<Ship> list,
                                         Integer pageNumber,
                                         Integer pageSize,
                                         String order) {

        if (pageSize == null && pageNumber == null && order == null) {
            return list.stream()
                    .limit(3)
                    .collect(Collectors.toList());
        }


        if (pageSize != null && pageNumber == null && order == null) {
            return list.stream()
                    .limit(pageSize)
                    .collect(Collectors.toList());
        }


        if (pageSize == null && pageNumber != null && order == null) {
            return list.stream()
                    .skip(pageNumber * 3)
                    .limit(3)
                    .collect(Collectors.toList());
        }


        if (pageSize == null && pageNumber == null) {
            return list.stream()
                    .sorted(getComparatorByOrder(order))
                    .limit(3) //default pageSize
                    .collect(Collectors.toList());
        }


        if (pageSize == null) {
            return list.stream()
                    .sorted(getComparatorByOrder(order))
                    .limit(3)
                    .collect(Collectors.toList());
        }


        if (pageNumber == null) {
            return list.stream()
                    .sorted(getComparatorByOrder(order))
                    .limit(pageSize)
                    .skip(pageSize)
                    .collect(Collectors.toList());
        }

        return list.stream()
                .sorted(getComparatorByOrder(order))
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public void BodyParamsNotNull(Ship ship) {
        if (ship.getName() == null
                || ship.getPlanet() == null
                || ship.getShipType() == null
                || ship.getProdDate() == null
                || ship.getSpeed() == null
                || ship.getCrewSize() == null) {
            throw new BadRequestException();
        }
    }
    public void PlanetNotEmpty(Ship ship) {
        if (ship.getPlanet().equals("")) {
            throw new BadRequestException();
        }
    }

    public void NameLength(Ship ship) {
        if (ship.getName() != null) {
            if (ship.getName().length() > 50) {
                throw new BadRequestException();
            }
        }
    }

    public void PlanetLength(Ship ship) {
        if (ship.getPlanet().length() > 50) {
            throw new BadRequestException();
        }
    }

    public void NameNotEmpty(Ship ship) {
        if (ship.getName().equals("")) {
            throw new BadRequestException();
        }
    }
    public void CrewSizeCorrect(Ship ship) {
        if (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999) {
            throw new BadRequestException();
        }
    }



    public void SpeedIsCorrect(Ship ship) {
        if (ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99) {
            throw new BadRequestException();
        }
    }



    public void ProdDateParamIsCorrect(Ship ship) {
        if (ship.getProdDate().getTime() < 0) {
            throw new BadRequestException();
        }

        Calendar dateMinCal = Calendar.getInstance();
        Calendar dateMaxCal = Calendar.getInstance();

        dateMinCal.set(2800, Calendar.JANUARY, 1);
        dateMaxCal.set(3019, Calendar.DECEMBER, 31);

        if (ship.getProdDate().getTime() < dateMinCal.getTimeInMillis()
                || ship.getProdDate().getTime() > dateMaxCal.getTimeInMillis()) {
            throw new BadRequestException();
        }
    }

    public Double calculateRating(Ship ship) {
        if (ship.getProdDate() != null && ship.getSpeed() != null) {
            double k;
            boolean isUsed = false;

            if (ship.isUsed() != null) {
                isUsed = ship.isUsed();
            }

            if (isUsed) {
                k = 0.5;
            } else {
                k = 1D;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ship.getProdDate().getTime());
            int prodYear = calendar.get(Calendar.YEAR);

            double ratingNoRound = (80 * ship.getSpeed() * k) / (3019 - prodYear + 1);

            return (double) Math.round(ratingNoRound * 100) / 100;
        }
        return null;
    }

    public Ship BodyToCreate(Ship ship) {
        if (ship == null) {
            return null;
        }

        Ship newShip = new Ship();

// 		Если в запросе на создание корабля нет параметра “isUsed”, то считаем,
//		что пришло значение “false”
        if (ship.isUsed() != null) {
            newShip.setUsed(ship.isUsed());
        } else {
            newShip.setUsed(false);
        }

//		Мы не можем создать корабль, если:
//		- указаны не все параметры из Data Params (кроме isUsed);
        BodyParamsNotNull(ship);

//		- длина значения параметра “name” или “planet” превышает
//		размер соответствующего поля в БД (50 символов);
        NameLength(ship);
        PlanetLength(ship);

//		- значение параметра “name” или “planet” пустая строка;
        NameNotEmpty(ship);
        PlanetNotEmpty(ship);

//		- скорость или размер команды находятся вне заданных
//		пределов;
        SpeedIsCorrect(ship);
        CrewSizeCorrect(ship);

//		- “prodDate”:[Long] < 0;
//		- год производства находится вне заданных пределов.;
        ProdDateParamIsCorrect(ship);

        //OK. init params
        String name = ship.getName();
        String planet = ship.getPlanet();
        ShipType shipType = ship.getShipType();
        Date prodDate = ship.getProdDate();
        Double speed = ship.getSpeed();
        Integer crewSize = ship.getCrewSize();

        Double rating = calculateRating(ship);

        //set new params
        newShip.setName(name);
        newShip.setPlanet(planet);
        newShip.setShipType(shipType);
        newShip.setProdDate(prodDate);
        newShip.setSpeed(speed);
        newShip.setCrewSize(crewSize);
        newShip.setRating(rating);

        //OK:
        return newShip;
    }

    public boolean updateShip(Ship ship, Ship jsonBody) {
        try { //check for empty body:
            jsonBody.equals(new Ship()); //can't do it cause fields are null
        } catch (NullPointerException e) {
            return false;
        }


        if (ship != null) {
            if (jsonBody.getName() != null) {
                NameNotEmpty(jsonBody);
                NameLength(jsonBody);
                ship.setName(jsonBody.getName());
            }

            if (jsonBody.getPlanet() != null) {
                PlanetNotEmpty(jsonBody);
                NameLength(jsonBody);
                ship.setPlanet(jsonBody.getPlanet());
            }

            if (jsonBody.getShipType() != null) {
                ship.setShipType(jsonBody.getShipType());
            }

            if (jsonBody.getProdDate() != null) {
                ProdDateParamIsCorrect(jsonBody);
                ship.setProdDate(jsonBody.getProdDate());
            }

            if (jsonBody.isUsed() != null) {
                ship.setUsed(jsonBody.isUsed());
            } else {
                ship.setUsed(false);
            }

            if (jsonBody.getSpeed() != null) {
                ship.setSpeed(jsonBody.getSpeed());
            }

            if (jsonBody.getCrewSize() != null) {
                CrewSizeCorrect(jsonBody);
                ship.setCrewSize(jsonBody.getCrewSize());
            }

            ship.setRating(calculateRating(ship));
            return true;
        } else {
            return false;
        }
    }

    public Ship getShipById(Long id) {
        if (id == 0) {
            throw new BadRequestException();
        }

        return shipRepository.findAll().stream()
                .filter(ship -> ship.getId().equals(id))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }
}