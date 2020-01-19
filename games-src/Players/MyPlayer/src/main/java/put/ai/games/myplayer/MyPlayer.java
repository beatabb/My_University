/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

// TODO: dlaczego wyskakuje 'Oszyst!' ? modyfikowałam funkcję tryToDoAMove. zwraca ona my_move który przychodzi z checkSquere gdzie ma
// na sztywno wpisane 4 argumenty -> posprawdzać to
package put.ai.games.myplayer;

import java.util.List;
import java.util.Random;
import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;
import put.ai.games.pentago.impl.PentagoMove;

public class MyPlayer extends Player {

    int N;
    int W = ((N/2) + (((N/2) + 1)/2));
    int D = N/2;
    int S = N - W + 1; // ile jest możliwych ułożeń W-pionków pod rząd w jednej kolumnie/rzędzie

    // 0 - pionek przeciwnika, 1 - mój pionek ; -1 - puste pole
    private Random random = new Random(0xdeadbeef);

    public boolean checkField(int x, int y, int what_i_check){
    /*
        if(field(x,y) == what_i_check)
        return true
     */
        return true;
    }

    public boolean checkRow(int x1, int y1, int deng_or_poss){
        // sprawdzam wiersz o podanym wierzchołku początkowym (x1, y1) i długości=W
        /*
        Jesli checkForDanger -> deng_or_poss = 0;
        Jesli checkForPossibility -> deng_or_poss = 1;
         */
        int number_of_searched_counters = 0;
        int what_check_firstly = (deng_or_poss + 1)%2;
        for(int x=x1; x<(x1+W); x++){
            if(checkField(x, y1, what_check_firstly)==true){
                return false;
            }
            // jesli min 1xTRUE to oznacz brak deng_or_poss;
            // możemy zwrócić false;
            if(checkField(x, y1, deng_or_poss)==true){
                number_of_searched_counters = number_of_searched_counters + 1;
            }
            // dodatkow cały czas zliczamy czy zbierzemy min Wxdeng_or_poss
            // wowczas wystapilo deng_or_poss i zwracamy true
        }
        if(number_of_searched_counters>=D){
            return true;
        }
        else{
            return false;
        }

    }

    public boolean checkColumn(int x1, int y1, int deng_or_poss){
        // sprawdzam kolumnę o podanym wierzchołku początkowym (x1, y1) i długości=W
        /*
        Jesli checkForDanger -> deng_or_poss = 0;
        Jesli checkForPossibility -> deng_or_poss = 1;
         */
        int number_of_searched_counters = 0;
        int what_check_firstly = (deng_or_poss + 1)%2;
        for(int y=y1; y<(y1+W); y++){
            if(checkField(x1, y, what_check_firstly)==true){
                return false;
            }
            // jesli min 1xTRUE to oznacz brak deng_or_poss;
            // możemy zwrócić false;
            if(checkField(x1, y, deng_or_poss)==true){
                number_of_searched_counters = number_of_searched_counters + 1;
            }
            // dodatkow cały czas zliczamy czy zbierzemy min Wxdeng_or_poss
            // wowczas wystapilo deng_or_poss i zwracamy true
        }
        if(number_of_searched_counters>=D){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean checkForDanger(){
        for(int shift=0; shift<S; shift++){
            for(int r=0; r<N; r++){
                // check Row
                checkRow(shift, r, 0);
            }
            for(int c=0; c<N; c++){
                // check Column
                checkColumn(c, shift, 0);
            }
        }
        return true;
    }

    public boolean checkForPossibility(){
        for(int shift=0; shift<N; shift++){
            for(int r=0; r<N; r++){
                checkRow(shift, r, 1);
            }
            for(int c=0; c<N; c++){
                checkColumn(c, shift, 1);
            }
        }
        return true;
    }

    public int getRequiredNumberOfMyCounters(int number_of_fields, int half_or_all){
        if(half_or_all == 0){
            return number_of_fields/2;
        }
        else{
            return number_of_fields;
        }
    }
    
    public Move tryToDoAMove(PentagoMove my_move, Board b){

        List<Move> moves = b.getMovesFor(getColor());
        for (Move _m : moves) {
            PentagoMove possibly_move = (PentagoMove) _m;
            if(possibly_move.getPlaceX() == my_move.getPlaceX())
                return my_move;
        }
        return moves.get(8);
    }

    public Color opponentsColor(){
        Color c1 = Player.Color.PLAYER1;
        Color c2 = Player.Color.PLAYER2;

        Color myC = getColor();

        if(myC == c1)
            return c2;
        else
            return c1;
    }

    public Move checkSquare(int square, int half_or_all, Board b){
        List<Move> moves = b.getMovesFor(getColor());
        Move res = moves.get(7);
        // x1 i x2 - wierzchołkowe wartości dla danego (obramowania) kwadratu
        int x2 = D + square;
        int x1 = D - 1 - square;

        int x_to_put_counter = 5;
        int y_to_put_counter = 5;

        Color opponentsColor = opponentsColor();
        Color emptyField;
        emptyField = Player.Color.EMPTY;

        int number_of_my_counters = 0;
        int number_of_opponents_counters = 0;
        int number_of_counters = 0;

        // algorytm: 4 -> 4 + 8 = 12 -> 12 + 8 = 20 -> 20 + 8 = 28 -> 28 + 8 = 36 -> 36 + 8 = 44 ...
        int number_of_fields = (x2 - x1 + 1)*4 - 4;
        int required_number_of_my_counters = getRequiredNumberOfMyCounters(number_of_fields, half_or_all);

        // przeglądam wszystkie pola w danym (obramowaniu) kwadracie
        for(int i=x1; i<=(x2 - 1); i++){
            // czy tu jest mój pionek
            if(b.getState(i, x1) == getColor()){
                number_of_my_counters = number_of_my_counters + 1;
                number_of_counters = number_of_counters + 1;
            }
            // czy tu jest mój pionek
            if(b.getState(x1, 1 + i) == getColor()){
                number_of_my_counters = number_of_my_counters + 1;
                number_of_counters = number_of_counters + 1;
            }
            // czy tu jest mój pionek
            if(b.getState(i+1, x2) == getColor()){
                number_of_my_counters = number_of_my_counters + 1;
                number_of_counters = number_of_counters + 1;
            }
            // czy tu jest mój pionek
            if(b.getState(x2, i) == getColor()){
                number_of_my_counters = number_of_my_counters + 1;
                number_of_counters = number_of_counters + 1;
            }

            //czy tu jest pionek przeciwnika
            if(b.getState(i, x1) == opponentsColor){
                number_of_opponents_counters = number_of_opponents_counters + 1;
                number_of_counters = number_of_counters + 1;
            }
            //czy tu jest pionek przeciwnika
            if(b.getState(x1, i + 1) == opponentsColor){
                number_of_opponents_counters = number_of_opponents_counters + 1;
                number_of_counters = number_of_counters + 1;
            }
            //czy tu jest pionek przeciwnika
            if(b.getState(i + 1, x2) == opponentsColor){
                number_of_opponents_counters = number_of_opponents_counters + 1;
                number_of_counters = number_of_counters + 1;
            }
            //czy tu jest pionek przeciwnika
            if(b.getState(x2, i) == opponentsColor){
                number_of_opponents_counters = number_of_opponents_counters + 1;
                number_of_counters = number_of_counters + 1;
            }
            // czy tu jest puste pole
            if(b.getState(i, x1) == emptyField){
                x_to_put_counter = i;
                y_to_put_counter = x1;
            }
            if(b.getState(x1, i + 1) == emptyField){
                x_to_put_counter = x1;
                y_to_put_counter = i;
            }
            if(b.getState(i + 1, x2) == emptyField){
                x_to_put_counter = i;
                y_to_put_counter = x2;
            }
            if(b.getState(x2, i) == emptyField){
                x_to_put_counter = x2;
                y_to_put_counter = i;
            }
            // w checkFieldsach zliczyć ile jest moich/przeciwnika/w sumie pionków
        }

        if(number_of_my_counters<required_number_of_my_counters && number_of_fields>number_of_counters){
            // gdzieś tutaj mój postaw pionek
            PentagoMove my_move = new PentagoMove(x_to_put_counter, y_to_put_counter, 1, 1, 1, 5, getColor()) ;
            return tryToDoAMove(my_move, b);
            //return moves.get(5);

        }
        //PentagoMove my_move = new PentagoMove(x_to_put_counter, y_to_put_counter, 1, 1, 1, 0, getColor()) ;
        //return tryToDoAMove(my_move, b);
        return res;
    }

    public Move otherStrategy(Board b){
        List<Move> moves = b.getMovesFor(getColor());
        Move result = moves.get(7);
        Move res;
        for(int i=0; i<8; i++){
            // będę sprawdzać kwadraty od najbardziej wewnętrznego. ;
            // Będzie ich N/2 = D;
            // Drugi argument - czy dopełniam go do połowy (0) czy do całości (1)
            res = checkSquare(i, 0, b);
            if(res != result)
                return res;
        }
        for(int i=0; i<D; i++){
            res = checkSquare(i, 1, b);
            if(res != result)
                return res;
        }
        //res = checkSquare(1, 0, b);

        return result;
    }

    public void myMove(Board b){
        if(checkForDanger()==false)
        {
            if(checkForPossibility()==false)
            {
                otherStrategy(b);
            }
        }
        return;
    }

    @Override
    public String getName() {
        return "Gracz Naiwny 84868";
    }


    @Override
    public Move nextMove(Board b) {
        return otherStrategy(b);
        //List<Move> moves = b.getMovesFor(getColor());
        //return moves.get(random.nextInt(moves.size()));
    }
}


