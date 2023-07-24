package edu.uob;

import edu.uob.OXOMoveException.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ExampleControllerTests {
  private OXOModel model;
  private OXOController controller;

  // Make a new "standard" (3x3) board before running each test case (i.e. this method runs before every `@Test` method)
  // In order to test boards of different sizes, winning thresholds or number of players, create a separate test file (without this method in it !)
  @BeforeEach
  void setup() {
    model = new OXOModel(3, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    controller = new OXOController(model);
  }

  // This next method is a utility function that can be used by any of the test methods to _safely_ send a command to the controller
  void sendCommandToController(String command) {
    // Try to send a command to the server - call will timeout if it takes too long (in case the server enters an infinite loop)
    // Note: this is ugly code and includes syntax that you haven't encountered yet
    String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
    assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> controller.handleIncomingCommand(command), timeoutComment);
  }

  // Test simple move taking and cell claiming functionality
  @Test
  void testBasicMoveTaking() throws OXOMoveException {
    // Find out which player is going to make the first move
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a move
    sendCommandToController("a1");
    // Check that A1 (cell [0,0] on the board) is now "owned" by the first player
    String failedTestComment = "Cell a1 wasn't claimed by the first player";
    assertEquals(firstMovingPlayer, controller.gameModel.getCellOwner(0, 0), failedTestComment);
  }


  // Test out basic win detection
  @Test
  void testBasicWin() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player

    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);

  }

  @Test
  void testBasicWinCol() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("b1"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("c1"); // First player
    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }

  @Test
  void testBasicWinRog() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("c1"); // Second player
    sendCommandToController("c3"); // First player
    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }

  @Test
  void testBasicWinInRog() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    // Make a bunch of moves for the two players
    sendCommandToController("A1"); // First player
    OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("A3"); // Second player
    sendCommandToController("A2"); // First player
    sendCommandToController("B2"); // Second player
    sendCommandToController("B1"); // First player
    sendCommandToController("C1"); // First player
    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + secondMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(secondMovingPlayer, model.getWinner(), failedTestComment);
  }

  // Example of how to test for the throwing of exceptions
  @Test
  void testInvalidIdentifierException() throws OXOMoveException {
    // Check that the controller throws a suitable exception when it gets an invalid command
    String failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for command `abc123`";
    // The next lins is a bit ugly, but it is the easiest way to test exceptions (soz)
    assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController("abc123"), failedTestComment);
    failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for command '   '";
    assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController("   "), failedTestComment);
  }
  @Test
  void testInvalidLengthException() throws OXOMoveException {
    // Check that the controller throws a suitable exception when it gets a shorter length command
    String failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for command `a`";
    assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController("a"), failedTestComment);
    failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for command `1`";
    assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController("1"), failedTestComment);
    failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for command ``";
    assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController(""), failedTestComment);
    failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for command ` `";
    assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController(" "), failedTestComment);
  }

  @Test
  void testInvalidIdentifierCharacterException() throws OXOMoveException {
    // Check that the controller throws a suitable exception when it gets an invalid character
    String failedTestComment = "Controller failed to throw an InvalidIdentifierCharacterException for command `.1`";
    assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController(".1"), failedTestComment);
    failedTestComment = "Controller failed to throw an InvalidIdentifierCharacterException for command '.''";
    assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController(".'"), failedTestComment);
    failedTestComment = "Controller failed to throw an InvalidIdentifierCharacterException for command 'a.'";
    assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("a."), failedTestComment);
    failedTestComment = "Controller failed to throw an InvalidIdentifierCharacterException for command 'a '";
    assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("a "), failedTestComment);
    failedTestComment = "Controller failed to throw an InvalidIdentifierCharacterException for command ' 1'";
    assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController(" 1"), failedTestComment);
    failedTestComment = "Controller failed to throw an InvalidIdentifierCharacterException for command '  '";
    assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("  "), failedTestComment);
  }
  @Test
  void testOutsideCellRangeException() throws OXOMoveException {
    // Check that the controller throws a suitable exception when it gets an outside range command
    String failedTestComment = "Controller failed to throw an OutsideCellRangeException for command `d1`";
    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("d1"), failedTestComment);
    failedTestComment = "Controller failed to throw an OutsideCellRangeException for command `a4`";
    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("a4"), failedTestComment);
    failedTestComment = "Controller failed to throw an OutsideCellRangeException for command `z1`";
    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("z1"), failedTestComment);
    failedTestComment = "Controller failed to throw an OutsideCellRangeException for command `a0`";
    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("a0"), failedTestComment);
  }

  @Test
  void testCellAlreadyTakenException() throws OXOMoveException {
    // Check that the controller throws a suitable exception when the cell has been filled
    String failedTestComment = "`a1` already taken";
    sendCommandToController("a1");
    sendCommandToController("c2");
    assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController("a1"), failedTestComment);
    failedTestComment = "`c2` already taken";
    assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController("c2"), failedTestComment);
  }


  @Test
  void testDecreaseWtWhilePlaying() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    controller.decreaseWinThreshold();
    assertEquals(model.getWinThreshold(), 3);
  }

  @Test
  void testIncreaseWtWhilePlaying() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a bunch of moves for the two players
    controller.addRow();
    controller.addColumn();
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    controller.decreaseWinThreshold();
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player
    sendCommandToController("b3"); // Second player
    sendCommandToController("a4"); // First player
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }

  @Test
  void testIncreaseBdWhilePlaying() throws OXOMoveException {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.addRow();
    controller.addRow();
    sendCommandToController("d2"); // First player
    sendCommandToController("c2"); // Second player
    controller.addColumn();
    sendCommandToController("d3"); // First player
    sendCommandToController("c3"); // Second player
    sendCommandToController("d4"); // First player
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
    assertEquals(model.getNumberOfRows(),5);
    assertEquals(model.getNumberOfColumns(),4);
  }

  @Test
  void testAddRowSuspendLimitation() throws OXOMoveException{
    //failed to add row than 9
//    String failedTestComment = "Controller failed to throw an OutsideCellRangeException for command `j1`";
//    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("j1"), failedTestComment);
    controller.addRow();
    controller.addRow();
    controller.addRow();
    controller.addRow();
    controller.addRow();
    controller.addRow();
    controller.addRow();
    assertEquals(model.getNumberOfRows(),9);
//    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("j1"), failedTestComment);
  }

  @Test
  void testAddColSuspendLimitation() throws OXOMoveException{

    String failedTestComment = "Controller failed to throw an OutsideCellRangeException for command `a9`";
    controller.addColumn();
    controller.addColumn();
    controller.addColumn();
    controller.addColumn();
    controller.addColumn();
    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("a9"), failedTestComment);
  }

  @Test
  void testRemoveRowWhileFilled() throws OXOMoveException{
    // remove row while filled
    sendCommandToController("c3"); // First player
    controller.removeRow();
    String failedTestComment = "Controller failed to remove a row because 'c3' is filled";
    assertEquals(model.getNumberOfRows(), 3, failedTestComment);
  }
  @Test
  void testRemoveRowWhileFilledWithAddRow() throws OXOMoveException{
    //add row first and remove the row
    controller.addRow();
    sendCommandToController("d2"); // First player
    controller.removeRow();
    String failedTestComment = "Controller failed to remove a row because 'd2' is filled";
    assertEquals(model.getNumberOfRows(), 4, failedTestComment);
  }

  @Test
  void testRemoveColWhileFilled() throws OXOMoveException{
    //remove the col while filled
    sendCommandToController("c3"); // First player
    controller.removeColumn();
    String failedTestComment = "Controller failed to remove a row because 'c3' is filled";
    assertEquals(model.getNumberOfColumns(), 3, failedTestComment);
  }

  @Test
  void testRemoveColWhileFilledWithAddCol() throws OXOMoveException{
    //add col first and remove the col
    controller.addColumn();
    sendCommandToController("c4"); // First player
    controller.removeColumn();
    String failedTestComment = "Controller failed to remove a row because 'c4' is filled";
    assertEquals(model.getNumberOfColumns(), 4, failedTestComment);
  }

  @Test
  void testRemoveWithAddBoth() throws OXOMoveException{
    //add row and col first and remove them
    controller.addColumn();
    controller.addRow();
    String failedTestComment1 = "Controller failed to add";
    assertEquals(model.getNumberOfRows(), 4, failedTestComment1);
    assertEquals(model.getNumberOfColumns(), 4, failedTestComment1);
    controller.removeColumn();
    controller.removeRow();
    String failedTestComment2 = "Controller failed to remove";
    assertEquals(model.getNumberOfRows(), 3, failedTestComment2);
    assertEquals(model.getNumberOfColumns(), 3, failedTestComment2);
  }

  @Test
  void testRemoveColWhileFilledWithAddBoth() throws OXOMoveException{
    //add row and col first and fill it then remove them
    controller.addColumn();
    controller.addRow();
    sendCommandToController("d4"); // First player
    controller.removeColumn();
    controller.removeRow();
    String failedTestComment = "Controller failed to remove a row because 'd4' is filled";
    assertEquals(model.getNumberOfRows(), 4, failedTestComment);
    assertEquals(model.getNumberOfColumns(), 4, failedTestComment);
  }

  @Test
  void testAddWt() throws OXOMoveException{
    //increase winthreshold to 6 and decrease to 4
    controller.increaseWinThreshold();//4
    controller.increaseWinThreshold();//5
    controller.increaseWinThreshold();//6
    controller.decreaseWinThreshold();//5
    controller.decreaseWinThreshold();//4
    controller.addRow();
    controller.addColumn();
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player
    sendCommandToController("b3"); // Second player
    sendCommandToController("a4"); // First player
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }

  @Test
  void testAddOnePlayer() throws OXOMoveException {
    model.addPlayer(new OXOPlayer('Z'));
    // Make a bunch of moves for the three players
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    // Find out which player is going to make the third move (they should be the eventual winner)
    OXOPlayer thirdMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("c1"); // Third player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("c2"); // Third player
    sendCommandToController("b3"); // First player
    sendCommandToController("a3"); // Second player
    sendCommandToController("c3"); // Third player

    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + thirdMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(thirdMovingPlayer, model.getWinner(), failedTestComment);
    assertEquals(model.getNumberOfPlayers(),3);
  }

  @Test
  void testAddTwoPlayer() throws OXOMoveException {
    model.addPlayer(new OXOPlayer('Z'));
    model.addPlayer(new OXOPlayer('Y'));
    controller.addRow();
    controller.addRow();
    controller.addColumn();
    controller.addColumn();
    controller.increaseWinThreshold();
    // Make a bunch of moves for the four players
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    // Find out which player is going to make the third move (they should be the eventual winner)
    sendCommandToController("c1"); // Third player
    OXOPlayer fourthMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("d1"); // Fourth player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("c2"); // Third player
    sendCommandToController("d2"); // Fourth player
    sendCommandToController("a3"); // First player
    sendCommandToController("b3"); // Second player
    sendCommandToController("c3"); // Third player
    sendCommandToController("d3"); // Fourth player
    sendCommandToController("c4"); // First player
    sendCommandToController("a4"); // Second player
    sendCommandToController("b4"); // Third player
    sendCommandToController("d4"); // Fourth player

    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + fourthMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(fourthMovingPlayer, model.getWinner(), failedTestComment);
    assertEquals(model.getNumberOfPlayers(),4);
  }

  @Test
  void testAddSamePlayer() throws OXOMoveException {
    model.addPlayer(new OXOPlayer('X'));
    // Make a bunch of moves for the three players
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("a1"); // First player X
    sendCommandToController("a2"); // Second player O
    // Find out which player is going to make the third move (they should be the eventual winner)
    OXOPlayer thirdMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("a3"); // Third player X
    sendCommandToController("b1"); // First player X
    sendCommandToController("b2"); // Second player
    sendCommandToController("b3"); // Third player
    sendCommandToController("c1"); // First player
    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
    assertEquals(model.getNumberOfPlayers(),3);
  }

  @Test
  void testAddWhilePlayingPlayer() throws OXOMoveException {
    //add Player while playing
    // Make a bunch of moves for the three players
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    model.addPlayer(new OXOPlayer('Z'));
    sendCommandToController("c1"); // Second player
    // Find out which player is going to make the third move (they should be the eventual winner)
    OXOPlayer thirdMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("a3"); // Third player
    sendCommandToController("b2"); // First player
    sendCommandToController("c2"); // Second player
    sendCommandToController("b3"); // Third player
    controller.addRow();
    sendCommandToController("d1"); // First player
    sendCommandToController("d2"); // Second player
    sendCommandToController("c3"); // Third player

    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + thirdMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(thirdMovingPlayer, model.getWinner(), failedTestComment);
  }

  @Test
  void testGameDrawn() throws OXOMoveException{
    sendCommandToController("a1");
    sendCommandToController("a2");
    sendCommandToController("b1");
    sendCommandToController("b2");
    sendCommandToController("a3");
    sendCommandToController("b3");
    sendCommandToController("c2");
    sendCommandToController("c1");
    sendCommandToController("c3");
    assertTrue(model.isGameDrawn());
    controller.addRow();
    assertEquals(model.getWinner(), null);
    assertFalse(model.isGameDrawn());
    controller.removeRow();
    assertEquals(model.getNumberOfRows(),4);
//    assertTrue(model.isGameDrawn());
    controller.addColumn();
    assertEquals(model.getWinner(), null);
//    assertFalse(model.isGameDrawn());
    controller.removeColumn();
    assertEquals(model.getNumberOfColumns(),3);
//    assertTrue(model.isGameDrawn());
  }

  @Test
  void testGameWinTest() throws OXOMoveException{
    //when the game is win, you can not add row, add col, remove row, remove col
    //add player, increase wt, decrease wt, input command
    //the only thing is esc to reset
    sendCommandToController("a1");
    sendCommandToController("b1");
    sendCommandToController("b2");
    sendCommandToController("c2");
    sendCommandToController("c3");
    controller.addRow();
    assertEquals(model.getNumberOfRows(),4);
    controller.addColumn();
    assertEquals(model.getNumberOfColumns(),4);
    controller.removeRow();
    assertEquals(model.getNumberOfRows(),3);
    controller.removeColumn();
    assertEquals(model.getNumberOfColumns(),3);
    //FIXME
    model.addPlayer(new OXOPlayer('Z'));
//    System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbb");
//      System.out.println(model.getNumberOfPlayers());
    assertEquals(model.getNumberOfPlayers(),3);
    controller.increaseWinThreshold();
    assertEquals(model.getWinThreshold(),4);
    controller.decreaseWinThreshold();
    assertEquals(model.getWinThreshold(),3);
    assertEquals(model.getNumberOfPlayers(),3);
    assertNull(model.getCellOwner(0, 1));
    sendCommandToController("a2");
    assertNull(model.getCellOwner(0, 1));
  }

  @Test
  void testResetNotWin() throws OXOMoveException{
    controller.addRow();
    controller.addColumn();
    controller.increaseWinThreshold();
    sendCommandToController("c4");
    sendCommandToController("d3");
    assertEquals(model.getCellOwner(2,3),model.getPlayerByNumber(0));
    assertEquals(model.getCellOwner(3,2),model.getPlayerByNumber(1));
    assertEquals(model.getWinThreshold(),4);
    //reset
    controller.reset();
    assertEquals(model.getCellOwner(2,3),null);
    assertEquals(model.getCellOwner(3,2),null);
    assertEquals(model.getCellOwner(0,0),null);
    assertEquals(model.getWinThreshold(),4);
    assertEquals(model.getNumberOfRows(),4);
    assertEquals(model.getNumberOfColumns(),4);
  }

  @Test
  void testResetWhileWin() throws OXOMoveException{
    controller.addRow();
    controller.addColumn();
    controller.increaseWinThreshold();
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("c4");
    sendCommandToController("d3");
    sendCommandToController("c3");
    sendCommandToController("a4");
    sendCommandToController("c2");
    model.addPlayer(new OXOPlayer('Z'));
    sendCommandToController("b3");
    sendCommandToController("a1");
    sendCommandToController("c1");
//    model.addPlayer(new OXOPlayer('Z'));
    assertEquals(model.getCellOwner(2,3),model.getPlayerByNumber(0));
    assertEquals(model.getCellOwner(3,2),model.getPlayerByNumber(1));
    assertEquals(model.getWinThreshold(),4);
    assertEquals(firstMovingPlayer, model.getWinner());
    assertEquals(model.getNumberOfPlayers(), 3);
    //reset
    controller.reset();
    assertEquals(model.getCellOwner(2,3),null);
    assertEquals(model.getCellOwner(3,2),null);
    assertEquals(model.getCellOwner(0,0),null);
    assertEquals(model.getWinThreshold(),4);
    assertEquals(model.getNumberOfRows(),4);
    assertEquals(model.getNumberOfColumns(),4);
    assertEquals(model.getNumberOfPlayers(), 3);
  }

  @Test
  void testResetDrawn() throws OXOMoveException{
    sendCommandToController("a1");
    sendCommandToController("a2");
    sendCommandToController("b1");
    sendCommandToController("b2");
    sendCommandToController("a3");
    sendCommandToController("b3");
    sendCommandToController("c2");
    sendCommandToController("c1");
    sendCommandToController("c3");
    assertTrue(model.isGameDrawn());
    controller.reset();
    assertFalse(model.isGameDrawn());
    for (int i = 0; i < model.getNumberOfRows() - 1; i++) {
      for (int j = 0; j < model.getNumberOfColumns() - 1; j++) {
        assertNull(model.getCellOwner(i,j));
      }
    }
  }
  @Test
  void testSetPlayer(){
    OXOPlayer testPlayer = new OXOPlayer('Z');
    assertEquals('Z',testPlayer.getPlayingLetter());
    testPlayer.setPlayingLetter('A');
    assertEquals('A',testPlayer.getPlayingLetter());
  }
}
