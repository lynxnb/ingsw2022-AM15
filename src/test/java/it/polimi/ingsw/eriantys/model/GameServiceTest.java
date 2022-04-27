package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.actions.StudentsMovement;
import it.polimi.ingsw.eriantys.model.entities.*;
import it.polimi.ingsw.eriantys.model.enums.GameMode;
import it.polimi.ingsw.eriantys.model.enums.HouseColor;
import it.polimi.ingsw.eriantys.model.enums.TowerColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

  @Test
  void pickAssistantCard() {
    RuleBook mockRule = mock(RuleBook.class);
    Player player = new Player(mockRule, "nick", TowerColor.WHITE, new Students());

    GameService.pickAssistantCard(player, 9);
    assertTrue(player.getChosenCard().isPresent());
    assertEquals(player.getChosenCard().get().value, player.getTurnPriority());
    assertEquals(player.getChosenCard().get().movement, player.getMaxMovement());
    try {
      GameService.pickAssistantCard(player, 10);
    } catch (Exception e) {
      Logger.info("Index out of bound");
    }
    GameService.pickAssistantCard(player, 5);
    assertTrue(player.getChosenCard().isPresent());
    assertEquals(player.getChosenCard().get().value, player.getTurnPriority());
    assertEquals(player.getChosenCard().get().movement, player.getMaxMovement());
  }

  @Test
  void pickCloud() {
    Cloud cloud = new Cloud(new Students());
    Dashboard dashboard = new Dashboard(new Students(), 8, TowerColor.WHITE);
    Students temp = new Students();
    temp.addStudent(HouseColor.PINK);
    temp.addStudent(HouseColor.PINK);
    temp.addStudent(HouseColor.PINK);
    cloud.setStudents(new Students(temp));

    GameService.pickCloud(cloud, dashboard);

    Arrays.stream(HouseColor.values()).forEach((color) ->
            assertEquals(0, cloud.getStudents().getCount(color))
    );
    Arrays.stream(HouseColor.values()).forEach((color) ->
            assertEquals(temp.getCount(color), dashboard.getEntrance().getCount(color))
    );
  }

  @Test
  public void placeStudents() {
    Students temp = new Students();
    temp.addStudent(HouseColor.PINK);
    temp.addStudent(HouseColor.PINK);
    Students src = new Students(temp);
    Students dest = new Students();

    StudentsMovement move = new StudentsMovement(temp, src, dest);

    // src has 2 PINKS -> dest who has 0
    assertTrue(GameService.placeStudents(move));
    assertEquals(0, src.getCount(HouseColor.PINK));
    assertEquals(2, dest.getCount(HouseColor.PINK));

    Island island = new Island();
    move = new StudentsMovement(temp, src, island);
    src.addStudent(HouseColor.PINK);
    src.addStudent(HouseColor.PINK);

    // src has 2 PINKS -> island who has 0
    assertTrue(GameService.placeStudents(move));
    assertEquals(0, src.getCount(HouseColor.PINK));
    assertEquals(2, island.getStudents().getCount(HouseColor.PINK));

    // src has 1 PINKS
    src.addStudent(HouseColor.PINK);
    assertFalse(GameService.placeStudents(move));
    assertEquals(1, src.getCount(HouseColor.PINK));
    assertEquals(2, island.getStudents().getCount(HouseColor.PINK));

  }

  @Test
  public void refillCloud() {
    StudentBag studentBag = new StudentBag();
    Logger.debug(studentBag.getStudents());
    List<Cloud> clouds = new ArrayList<>();
    clouds.add(new Cloud(new Students()));
    clouds.add(new Cloud(new Students()));

    List<Students> studentsList = new ArrayList<>();
    Students temp = new Students();
    temp.addStudent(HouseColor.PINK);
    studentsList.add(new Students(temp));
    temp.addStudent(HouseColor.PINK);
    studentsList.add(new Students(temp));

    // Moving 1 PINK -> cloud1, 2 PINKS -> cloud2
    GameService.refillClouds(studentBag, clouds, studentsList);

    // Bag is empty, no operation has been executed so the clouds are still empty
    clouds.forEach(cloud -> {
//      Logger.debug(cloud.getStudents());
      assertTrue(cloud.isEmpty());
    });

    temp.setStudents(new Students());
    int initialAmountInBag = 10;
    temp.addStudents(HouseColor.PINK, initialAmountInBag);
    studentBag.getStudents().setStudents(temp);

    // Moving 1 PINK -> cloud1, 2 PINKS -> cloud2
    GameService.refillClouds(studentBag, clouds, studentsList);

    Logger.debug(clouds.get(0).getStudents());
    assertFalse(clouds.get(0).isEmpty());
    Logger.debug(clouds.get(1).getStudents());
    assertFalse(clouds.get(1).isEmpty());
    int totalStudentsMoved = clouds.get(0).getStudents().getCount() + clouds.get(1).getStudents().getCount();
    Logger.debug(studentBag.getStudents());
    assertEquals(totalStudentsMoved, initialAmountInBag - studentBag.getStudents().getCount());
  }

  @Test
  public void MotherNatureIslandLocked() {
    RuleBook rules = RuleBook.makeRules(GameMode.NORMAL, 2);
    PlayingField field = new PlayingField(rules);
    List<Player> players = new ArrayList<>();
    players.add(new Player(rules, "gino", TowerColor.BLACK, new Students()));
    players.add(new Player(rules, "franco", TowerColor.WHITE, new Students()));

    TowerColor oldIslandColor = field.getIsland(1).getTowerColor().get();
    int oldIslandAmount = field.getIslandsAmount();
    int blackPTowerCount = players.get(0).getDashboard().towerCount();
    int whitePTowerCount = players.get(1).getDashboard().towerCount();
    Logger.debug("\nold amount " + oldIslandAmount);
    field.getIsland(1).setLocked(true);
    GameService.applyMotherNatureEffect(1, field, players);
    Logger.debug("\nnew amount " + field.getIslandsAmount());

    assertEquals(oldIslandAmount, field.getIslandsAmount());
    assertEquals(oldIslandColor, field.getIsland(1).getTowerColor());
    assertEquals(blackPTowerCount, players.get(0).getDashboard().towerCount());
    assertEquals(whitePTowerCount, players.get(1).getDashboard().towerCount());
  }

  @Test
  void MotherNatureNoMostInfluential() {
    RuleBook rules = RuleBook.makeRules(GameMode.NORMAL, 2);
    PlayingField field1 = new PlayingField(rules);
    PlayingField fieldMock = spy(field1);
    doReturn(Optional.empty()).when(fieldMock).getMostInfluential(1);
    List<Player> players = new ArrayList<>();
    players.add(new Player(rules, "gino", TowerColor.BLACK, new Students()));
    players.add(new Player(rules, "franco", TowerColor.WHITE, new Students()));
    Logger.debug("most influential in : " + fieldMock);

    int oldIslandAmount = fieldMock.getIslandsAmount();
    TowerColor oldIslandColor = fieldMock.getIsland(1).getTowerColor().get();
    int blackPTowerCount = players.get(0).getDashboard().towerCount();
    int whitePTowerCount = players.get(1).getDashboard().towerCount();
    Logger.debug("\nold amount " + oldIslandAmount);
    GameService.applyMotherNatureEffect(1, fieldMock, players);
    Logger.debug("\nnew amount " + fieldMock.getIslandsAmount());

    assertEquals(oldIslandAmount, fieldMock.getIslandsAmount());
    assertEquals(oldIslandColor, fieldMock.getIsland(1).getTowerColor());
    assertEquals(blackPTowerCount, players.get(0).getDashboard().towerCount());
    assertEquals(whitePTowerCount, players.get(1).getDashboard().towerCount());
  }

  @Test
  void MotherNatureEffect() {
    RuleBook rules = RuleBook.makeRules(GameMode.NORMAL, 2);
    PlayingField field = new PlayingField(rules);
    PlayingField fieldMock = spy(field);
    doReturn(Optional.of(TowerColor.WHITE)).when(fieldMock).getMostInfluential(1);
    List<Player> players = new ArrayList<>();
    players.add(new Player(rules, "gino", TowerColor.BLACK, new Students()));
    players.add(new Player(rules, "franco", TowerColor.WHITE, new Students()));
    fieldMock.getIsland(1).setTowerColor(TowerColor.BLACK);
    fieldMock.getIsland(1).setTowerCount(1);
    fieldMock.getIsland(2).setTowerColor(TowerColor.WHITE);
    fieldMock.getIsland(2).setTowerCount(1);
    fieldMock.getIsland(0).setTowerColor(TowerColor.WHITE);
    fieldMock.getIsland(0).setTowerCount(1);

    int oldIslandAmount = fieldMock.getIslandsAmount();
    int blackPTowerCount = players.get(0).getDashboard().towerCount();
    int whitePTowerCount = players.get(1).getDashboard().towerCount();
    Logger.debug("\nold amount " + oldIslandAmount);
    GameService.applyMotherNatureEffect(1, fieldMock, players);
    Logger.debug("\nnew amount " + fieldMock.getIslandsAmount());

    assertEquals(oldIslandAmount - 2, fieldMock.getIslandsAmount());
    if (fieldMock.getIsland(0).getTowerColor().isPresent())
      assertEquals(TowerColor.WHITE, fieldMock.getIsland(0).getTowerColor().get());
    assertEquals(blackPTowerCount + 1, players.get(0).getDashboard().towerCount());
    assertEquals(whitePTowerCount - 1, players.get(1).getDashboard().towerCount());
  }


}