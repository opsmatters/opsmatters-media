/*
 * Copyright 2022 Gerald Curley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opsmatters.media.db.dao.admin;

import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.logging.Logger;
import com.opsmatters.media.model.admin.Note;
import com.opsmatters.media.model.admin.NoteType;
import com.opsmatters.media.util.StringUtils;

/**
 * DAO that provides operations on the NOTES table in the database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class NoteDAO extends AdminDAO<Note>
{
    private static final Logger logger = Logger.getLogger(NoteDAO.class.getName());

    /**
     * The query to use to select a note from the NOTES table by id.
     */
    private static final String GET_BY_ID_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, VALUE "
      + "FROM NOTES WHERE ID=?";

    /**
     * The query to use to select a note from the NOTES table by code.
     */
    private static final String GET_BY_CODE_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, VALUE "
      + "FROM NOTES WHERE CODE=? AND TYPE=?";

    /**
     * The query to use to insert a note into the NOTES table.
     */
    private static final String INSERT_SQL =  
      "INSERT INTO NOTES"
      + "( ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, VALUE )"
      + "VALUES"
      + "( ?, ?, ?, ?, ?, ? )";

    /**
     * The query to use to update a note in the NOTES table.
     */
    private static final String UPDATE_SQL =  
      "UPDATE NOTES SET UPDATED_DATE=?, VALUE=? "
      + "WHERE ID=?";

    /**
     * The query to use to select the notes from the NOTES table.
     */
    private static final String LIST_SQL =  
      "SELECT ID, CREATED_DATE, UPDATED_DATE, CODE, TYPE, VALUE "
      + "FROM NOTES";

    /**
     * The query to use to get the count of notes from the NOTES table.
     */
    private static final String COUNT_SQL =  
      "SELECT COUNT(*) FROM NOTES";

    /**
     * The query to use to delete a note from the NOTES table.
     */
    private static final String DELETE_SQL =  
      "DELETE FROM NOTES WHERE CODE=? AND TYPE=?";

    /**
     * Constructor that takes a DAO factory.
     */
    public NoteDAO(AdminDAOFactory factory)
    {
        super(factory, "NOTES");
    }

    /**
     * Defines the columns and indices for the NOTES table.
     */
    @Override
    protected void defineTable()
    {
        table.addColumn("ID", Types.VARCHAR, 36, true);
        table.addColumn("CREATED_DATE", Types.TIMESTAMP, true);
        table.addColumn("UPDATED_DATE", Types.TIMESTAMP, false);
        table.addColumn("CODE", Types.VARCHAR, 5, true);
        table.addColumn("TYPE", Types.VARCHAR, 20, true);
        table.addColumn("VALUE", Types.VARCHAR, 128, true);
        table.setPrimaryKey("NOTES_PK", new String[] {"ID"});
        table.addIndex("NOTES_CODE_IDX", new String[] {"CODE", "TYPE"});
        table.setInitialised(true);
    }

    /**
     * Returns a note from the NOTES table by id.
     */
    public synchronized Note getById(String id) throws SQLException
    {
        Note ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByIdStmt == null)
            getByIdStmt = prepareStatement(getConnection(), GET_BY_ID_SQL);
        clearParameters(getByIdStmt);

        ResultSet rs = null;

        try
        {
            getByIdStmt.setString(1, id);
            getByIdStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByIdStmt.executeQuery();
            while(rs.next())
            {
                Note note = new Note();
                note.setId(rs.getString(1));
                note.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                note.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                note.setCode(rs.getString(4));
                note.setType(NoteType.valueOf(rs.getString(5)));
                note.setValue(rs.getString(6));
                ret = note;
            }
        }
        finally
        {
            try
            {
                if(rs != null)
                    rs.close();
            }
            catch (SQLException ex) 
            {
            } 
        }

        postQuery();

        return ret;
    }

    /**
     * Returns a note from the NOTES table by type.
     */
    public synchronized Note getByCode(String code, NoteType type) throws SQLException
    {
        Note ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(getByCodeStmt == null)
            getByCodeStmt = prepareStatement(getConnection(), GET_BY_CODE_SQL);
        clearParameters(getByCodeStmt);

        ResultSet rs = null;

        try
        {
            getByCodeStmt.setString(1, code);
            getByCodeStmt.setString(2, type.name());
            getByCodeStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = getByCodeStmt.executeQuery();
            while(rs.next())
            {
                Note note = new Note();
                note.setId(rs.getString(1));
                note.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                note.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                note.setCode(rs.getString(4));
                note.setType(NoteType.valueOf(rs.getString(5)));
                note.setValue(rs.getString(6));
                ret = note;
            }
        }
        finally
        {
            try
            {
                if(rs != null)
                    rs.close();
            }
            catch (SQLException ex) 
            {
            } 
        }

        postQuery();

        return ret;
    }

    /**
     * Stores the given note in the NOTES table.
     */
    public synchronized void add(Note note) throws SQLException
    {
        if(!hasConnection() || note == null)
            return;

        if(insertStmt == null)
            insertStmt = prepareStatement(getConnection(), INSERT_SQL);
        clearParameters(insertStmt);

        try
        {
            insertStmt.setString(1, note.getId());
            insertStmt.setTimestamp(2, new Timestamp(note.getCreatedDateMillis()), UTC);
            insertStmt.setTimestamp(3, new Timestamp(note.getUpdatedDateMillis()), UTC);
            insertStmt.setString(4, note.getCode());
            insertStmt.setString(5, note.getType().name());
            insertStmt.setString(6, note.getValue());
            insertStmt.executeUpdate();

            logger.info(String.format("Created note %s in NOTES", note.getId()));
        }
        catch(SQLException ex)
        {
            // SQLite closes the statement on an exception
            if(getDriver().closeOnException())
            {
                closeStatement(insertStmt);
                insertStmt = null;
            }

            // Unique constraint violated means that the note already exists
            if(!getDriver().isConstraintViolation(ex))
                throw ex;
        }
    }

    /**
     * Updates the given note in the NOTES table.
     */
    public synchronized void update(Note note) throws SQLException
    {
        if(!hasConnection() || note == null)
            return;

        if(updateStmt == null)
            updateStmt = prepareStatement(getConnection(), UPDATE_SQL);
        clearParameters(updateStmt);

        updateStmt.setTimestamp(1, new Timestamp(note.getUpdatedDateMillis()), UTC);
        updateStmt.setString(2, note.getValue());
        updateStmt.setString(3, note.getId());
        updateStmt.executeUpdate();

        logger.info(String.format("Updated note %s in NOTES", note.getId()));
    }

    /**
     * Adds or Updates the given note in the NOTES table.
     */
    public boolean upsert(Note note) throws SQLException
    {
        boolean ret = false;

        Note existing = getById(note.getId());
        if(existing != null)
        {
            update(note);
        }
        else
        {
            add(note);
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the notes from the NOTES table.
     */
    public synchronized List<Note> list() throws SQLException
    {
        List<Note> ret = null;

        if(!hasConnection())
            return ret;

        preQuery();
        if(listStmt == null)
            listStmt = prepareStatement(getConnection(), LIST_SQL);
        clearParameters(listStmt);

        ResultSet rs = null;

        try
        {
            listStmt.setQueryTimeout(QUERY_TIMEOUT);
            rs = listStmt.executeQuery();
            ret = new ArrayList<Note>();
            while(rs.next())
            {
                Note note = new Note();
                note.setId(rs.getString(1));
                note.setCreatedDateMillis(rs.getTimestamp(2, UTC).getTime());
                note.setUpdatedDateMillis(rs.getTimestamp(3, UTC).getTime());
                note.setCode(rs.getString(4));
                note.setType(NoteType.valueOf(rs.getString(5)));
                note.setValue(rs.getString(6));
                ret.add(note);
            }
        }
        finally
        {
            try
            {
                if(rs != null)
                    rs.close();
            }
            catch (SQLException ex) 
            {
            } 
        }

        postQuery();

        return ret;
    }

    /**
     * Returns the count of notes from the table.
     */
    public int count() throws SQLException
    {
        if(!hasConnection())
            return -1;

        if(countStmt == null)
            countStmt = prepareStatement(getConnection(), COUNT_SQL);
        clearParameters(countStmt);

        countStmt.setQueryTimeout(QUERY_TIMEOUT);
        ResultSet rs = countStmt.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    /**
     * Removes the given note from the NOTES table.
     */
    public synchronized void delete(Note note) throws SQLException
    {
        if(!hasConnection() || note == null)
            return;

        if(deleteStmt == null)
            deleteStmt = prepareStatement(getConnection(), DELETE_SQL);
        clearParameters(deleteStmt);

        deleteStmt.setString(1, note.getId());
        deleteStmt.executeUpdate();

        logger.info(String.format("Deleted note %s in NOTES", note.getId()));
    }

    /**
     * Close any resources associated with this DAO.
     */
    @Override
    protected void close()
    {
        closeStatement(getByIdStmt);
        getByIdStmt = null;
        closeStatement(getByCodeStmt);
        getByCodeStmt = null;
        closeStatement(insertStmt);
        insertStmt = null;
        closeStatement(updateStmt);
        updateStmt = null;
        closeStatement(listStmt);
        listStmt = null;
        closeStatement(countStmt);
        countStmt = null;
        closeStatement(deleteStmt);
        deleteStmt = null;
    }

    private PreparedStatement getByIdStmt;
    private PreparedStatement getByCodeStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement listStmt;
    private PreparedStatement countStmt;
    private PreparedStatement deleteStmt;
}
