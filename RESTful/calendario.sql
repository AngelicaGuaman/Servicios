SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `calendarBBDD` ;
CREATE SCHEMA IF NOT EXISTS `calendarBBDD` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `calendarBBDD` ;

-- -----------------------------------------------------
-- Table `calendarBBDD`.`Usuario`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `calendarBBDD`.`Usuario` ;

CREATE  TABLE IF NOT EXISTS `calendarBBDD`.`Usuario` (
  `idUsuario` INT NOT NULL ,
  `nombre` VARCHAR(45) NOT NULL ,
  `apellido1` VARCHAR(45) NOT NULL ,
  `apellido2` VARCHAR(45) NOT NULL ,
  `telefono` VARCHAR(9) NULL ,
  `direccion` VARCHAR(45) NULL ,
  PRIMARY KEY (`idUsuario`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `calendarBBDD`.`Calendario`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `calendarBBDD`.`Calendario` ;

CREATE  TABLE IF NOT EXISTS `calendarBBDD`.`Calendario` (
  `idCalendario` INT NOT NULL ,
  `nombreCalendario` VARCHAR(45) NOT NULL ,
  `visibilidad` INT NOT NULL ,
  `Usuario_idUsuario` INT NOT NULL ,
  PRIMARY KEY (`idCalendario`) ,
  INDEX `fk_Calendario_Usuario_idx` (`Usuario_idUsuario` ASC) ,
  CONSTRAINT `fk_Calendario_Usuario`
    FOREIGN KEY (`Usuario_idUsuario` )
    REFERENCES `calendarBBDD`.`Usuario` (`idUsuario` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `calendarBBDD`.`Cita`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `calendarBBDD`.`Cita` ;

CREATE  TABLE IF NOT EXISTS `calendarBBDD`.`Cita` (
  `idCita` INT NOT NULL ,
  `nombreCita` VARCHAR(45) NOT NULL ,
  `fecha` DATE NOT NULL ,
  `Calendario_idCalendario` INT NOT NULL ,
  PRIMARY KEY (`idCita`) ,
  INDEX `fk_Cita_Calendario1_idx` (`Calendario_idCalendario` ASC) ,
  CONSTRAINT `fk_Cita_Calendario1`
    FOREIGN KEY (`Calendario_idCalendario` )
    REFERENCES `calendarBBDD`.`Calendario` (`idCalendario` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `calendarBBDD` ;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
