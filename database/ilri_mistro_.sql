-- phpMyAdmin SQL Dump
-- version 3.5.8.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Sep 15, 2013 at 07:30 PM
-- Server version: 5.5.32-0ubuntu0.13.04.1
-- PHP Version: 5.4.9-4ubuntu2.3

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `ilri_mistro`
--

-- --------------------------------------------------------

--
-- Table structure for table `breed`
--

CREATE TABLE IF NOT EXISTS `breed` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=21 ;

--
-- Dumping data for table `breed`
--

INSERT INTO `breed` (`id`, `name`) VALUES
(4, 'Aberdeen Angus'),
(10, 'Ankole'),
(1, 'Ayrshire'),
(2, 'Boran'),
(12, 'Brahman'),
(19, 'Brown Swiss'),
(3, 'Charolais'),
(18, 'Fleckviah'),
(6, 'Friesian'),
(7, 'Guernsey'),
(8, 'Hereford'),
(9, 'Jersey'),
(11, 'Limousine'),
(17, 'Nganda'),
(14, 'Piemontese'),
(15, 'Red Poll'),
(16, 'Sahiwal'),
(13, 'Santa Getrude'),
(5, 'Simmental'),
(20, 'Zebu');

-- --------------------------------------------------------

--
-- Table structure for table `country`
--

CREATE TABLE IF NOT EXISTS `country` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_estonian_ci AUTO_INCREMENT=4 ;

--
-- Dumping data for table `country`
--

INSERT INTO `country` (`id`, `name`) VALUES
(1, 'Kenya'),
(2, 'Tanzania'),
(3, 'Uganda');

-- --------------------------------------------------------

--
-- Table structure for table `cow`
--

CREATE TABLE IF NOT EXISTS `cow` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `farmer_id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `ear_tag_number` varchar(255) NOT NULL,
  `date_of_birth` datetime DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `age_type` enum('Years','Weeks','Days') DEFAULT NULL,
  `sex` enum('Female','Male') NOT NULL,
  `sire_id` int(11) DEFAULT NULL,
  `dam_id` int(11) DEFAULT NULL,
  `straw_id` int(11) DEFAULT NULL,
  `embryo_id` int(11) DEFAULT NULL,
  `date_added` datetime NOT NULL,
  `service_type` enum('Bull','AI','ET') DEFAULT NULL,
  `country_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `farmer_id_2` (`farmer_id`,`name`),
  KEY `farmer_id` (`farmer_id`),
  KEY `sire_id` (`sire_id`),
  KEY `dam_id` (`dam_id`),
  KEY `straw_id` (`straw_id`),
  KEY `embryo_id` (`embryo_id`),
  KEY `country_id` (`country_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=8 ;

--
-- Dumping data for table `cow`
--

INSERT INTO `cow` (`id`, `farmer_id`, `name`, `ear_tag_number`, `date_of_birth`, `age`, `age_type`, `sex`, `sire_id`, `dam_id`, `straw_id`, `embryo_id`, `date_added`, `service_type`, `country_id`) VALUES
(5, 12, 'Cow1', 'jascow1', '2008-03-11 00:00:00', 5, 'Years', 'Male', NULL, NULL, NULL, NULL, '2013-09-15 19:21:31', 'AI', NULL),
(6, 12, 'Cow2', 'jascow2', '0000-00-00 00:00:00', 8, 'Weeks', 'Female', NULL, NULL, NULL, NULL, '2013-09-15 19:21:32', 'Bull', NULL),
(7, 14, 'Tesr', 'rdx', '0000-00-00 00:00:00', 2, 'Days', 'Female', NULL, NULL, NULL, NULL, '2013-09-15 19:27:43', 'Bull', 1);

-- --------------------------------------------------------

--
-- Table structure for table `cow_breed`
--

CREATE TABLE IF NOT EXISTS `cow_breed` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cow_id` int(11) NOT NULL,
  `breed_id` int(11) NOT NULL,
  `date_added` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `cow_id` (`cow_id`),
  KEY `breed_id` (`breed_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=10 ;

--
-- Dumping data for table `cow_breed`
--

INSERT INTO `cow_breed` (`id`, `cow_id`, `breed_id`, `date_added`) VALUES
(8, 5, 3, '2013-09-15 19:21:31'),
(9, 5, 5, '2013-09-15 19:21:31');

-- --------------------------------------------------------

--
-- Table structure for table `cow_deformity`
--

CREATE TABLE IF NOT EXISTS `cow_deformity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cow_id` int(11) NOT NULL,
  `deformity_id` int(11) NOT NULL,
  `date_added` datetime NOT NULL,
  `specification` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `cow_id` (`cow_id`),
  KEY `deformity_id` (`deformity_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

-- --------------------------------------------------------

--
-- Table structure for table `cow_event`
--

CREATE TABLE IF NOT EXISTS `cow_event` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cow_id` int(11) NOT NULL,
  `event_id` int(11) NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `event_date` date NOT NULL,
  `date_added` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `cow_id` (`cow_id`,`event_id`),
  KEY `event_id` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `deformity`
--

CREATE TABLE IF NOT EXISTS `deformity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

--
-- Dumping data for table `deformity`
--

INSERT INTO `deformity` (`id`, `name`) VALUES
(3, 'Abnormal teat number'),
(1, 'Blind'),
(5, 'Clef palate'),
(4, 'Crooked feet'),
(2, 'Lame'),
(6, 'Other');

-- --------------------------------------------------------

--
-- Table structure for table `embryo`
--

CREATE TABLE IF NOT EXISTS `embryo` (
  `id` int(11) NOT NULL,
  `embryo_no` varchar(100) NOT NULL,
  `dam_id` int(11) DEFAULT NULL,
  `sire_id` int(11) DEFAULT NULL,
  `date_added` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `embryo_no` (`embryo_no`),
  KEY `dam_id` (`dam_id`,`sire_id`),
  KEY `sire_id` (`sire_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `event`
--

CREATE TABLE IF NOT EXISTS `event` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=10 ;

--
-- Dumping data for table `event`
--

INSERT INTO `event` (`id`, `name`) VALUES
(1, 'Abortion'),
(9, 'Acquisition'),
(2, 'Birth'),
(4, 'Bloat'),
(6, 'Death'),
(7, 'Dry Off'),
(8, 'Sale'),
(5, 'Sickness'),
(3, 'Start of Lactation');

-- --------------------------------------------------------

--
-- Table structure for table `extension_personnel`
--

CREATE TABLE IF NOT EXISTS `extension_personnel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `date_added` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

--
-- Dumping data for table `extension_personnel`
--

INSERT INTO `extension_personnel` (`id`, `name`, `date_added`) VALUES
(5, 'Tom Muriranji', '2013-09-15 18:51:53');

-- --------------------------------------------------------

--
-- Table structure for table `farmer`
--

CREATE TABLE IF NOT EXISTS `farmer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `mobile_no` varchar(50) NOT NULL,
  `location_county` varchar(255) DEFAULT NULL,
  `location_district` varchar(255) DEFAULT NULL,
  `gps_longitude` varchar(15) DEFAULT NULL,
  `gps_latitude` varchar(15) DEFAULT NULL,
  `date_added` datetime NOT NULL,
  `extension_personnel_id` int(11) DEFAULT NULL,
  `sim_card_sn` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mobile_no` (`mobile_no`,`sim_card_sn`),
  KEY `extension_personnel_id` (`extension_personnel_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=15 ;

--
-- Dumping data for table `farmer`
--

INSERT INTO `farmer` (`id`, `name`, `mobile_no`, `location_county`, `location_district`, `gps_longitude`, `gps_latitude`, `date_added`, `extension_personnel_id`, `sim_card_sn`) VALUES
(12, 'Jason Rogena', '0715023805', NULL, NULL, '', '', '2013-09-15 19:21:31', 5, '89254029541005994302'),
(14, 'Jason Rogena', '0715023805', NULL, NULL, '', '', '2013-09-15 19:27:43', 5, '89254029541005994303');

-- --------------------------------------------------------

--
-- Table structure for table `milk_production`
--

CREATE TABLE IF NOT EXISTS `milk_production` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cow_id` int(11) NOT NULL,
  `time` enum('Morning','Afternoon','Evening','Combined') NOT NULL COMMENT '0 - morning, 1 - afternoot, 2 - evening, 3 - combined',
  `quantity` int(11) NOT NULL,
  `date_added` datetime NOT NULL,
  `date` date NOT NULL,
  `quantity_type` enum('Litres','KGs','Cups') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `cow_id_2` (`cow_id`,`time`,`date`),
  KEY `cow_id` (`cow_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `straw`
--

CREATE TABLE IF NOT EXISTS `straw` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `straw_number` varchar(100) NOT NULL,
  `sire_id` int(11) DEFAULT NULL,
  `date_added` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `straw_number` (`straw_number`),
  KEY `sire_id` (`sire_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `vet`
--

CREATE TABLE IF NOT EXISTS `vet` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `date_added` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `cow`
--
ALTER TABLE `cow`
  ADD CONSTRAINT `cow_ibfk_1` FOREIGN KEY (`farmer_id`) REFERENCES `farmer` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_ibfk_2` FOREIGN KEY (`sire_id`) REFERENCES `cow` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_ibfk_3` FOREIGN KEY (`dam_id`) REFERENCES `cow` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_ibfk_4` FOREIGN KEY (`straw_id`) REFERENCES `straw` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_ibfk_5` FOREIGN KEY (`embryo_id`) REFERENCES `embryo` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_ibfk_6` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `cow_breed`
--
ALTER TABLE `cow_breed`
  ADD CONSTRAINT `cow_breed_ibfk_1` FOREIGN KEY (`cow_id`) REFERENCES `cow` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_breed_ibfk_2` FOREIGN KEY (`breed_id`) REFERENCES `breed` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `cow_deformity`
--
ALTER TABLE `cow_deformity`
  ADD CONSTRAINT `cow_deformity_ibfk_1` FOREIGN KEY (`cow_id`) REFERENCES `cow` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_deformity_ibfk_2` FOREIGN KEY (`deformity_id`) REFERENCES `deformity` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `cow_event`
--
ALTER TABLE `cow_event`
  ADD CONSTRAINT `cow_event_ibfk_1` FOREIGN KEY (`cow_id`) REFERENCES `cow` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `cow_event_ibfk_2` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `embryo`
--
ALTER TABLE `embryo`
  ADD CONSTRAINT `embryo_ibfk_1` FOREIGN KEY (`dam_id`) REFERENCES `cow` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `embryo_ibfk_2` FOREIGN KEY (`sire_id`) REFERENCES `cow` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `farmer`
--
ALTER TABLE `farmer`
  ADD CONSTRAINT `farmer_ibfk_1` FOREIGN KEY (`extension_personnel_id`) REFERENCES `extension_personnel` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `milk_production`
--
ALTER TABLE `milk_production`
  ADD CONSTRAINT `milk_production_ibfk_1` FOREIGN KEY (`cow_id`) REFERENCES `cow` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `straw`
--
ALTER TABLE `straw`
  ADD CONSTRAINT `straw_ibfk_1` FOREIGN KEY (`sire_id`) REFERENCES `cow` (`id`) ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
