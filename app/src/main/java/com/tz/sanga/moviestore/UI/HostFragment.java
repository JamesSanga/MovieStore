package com.tz.sanga.moviestore.UI;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.tz.sanga.moviestore.Adapters.MoviesAdapter;
import com.tz.sanga.moviestore.BuildConfig;
import com.tz.sanga.moviestore.DI.MovieStore;
import com.tz.sanga.moviestore.Model.API.Connector;
import com.tz.sanga.moviestore.Model.API.Service;
import com.tz.sanga.moviestore.Model.Movie;
import com.tz.sanga.moviestore.Model.MoviesResponse;
import com.tz.sanga.moviestore.Presenters.Host.HostPresenter;
import com.tz.sanga.moviestore.Presenters.Host.HostView;
import com.tz.sanga.moviestore.R;
import com.tz.sanga.moviestore.Utils.MoviesScrollListener;
import com.tz.sanga.moviestore.ViewModel.HostViewModel;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HostFragment extends Fragment implements HostView{

    @Inject
    public SharedPreferences preferences;
    @Inject public Context context;

    @BindView(R.id.Movie_list) RecyclerView recyclerView;
    @BindView(R.id.Load_movies) ProgressBar progressBar;
    @BindView(R.id.refresh) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.view_to_navigate) ConstraintLayout layout;
    MoviesAdapter adapter;
    LinearLayoutManager layoutManager;
    final String [] moviesOptions ={"Popular movies", "Top rated movies"};
    AlertDialog.Builder mBuilder;
    Bundle bundle;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 20;
    private int currentPage = PAGE_START;
    HostPresenter presenter;
    int number;

    public HostFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_host, container, false);
        ButterKnife.bind(this, view);
        ((MovieStore)getActivity().getApplication()).getMyApplicationComponents().inject(this);
        bundle = new Bundle();
        setToolBar();
        initialize();
//        getData();
        presenter.getData();
        refreshPage();
        return view;
    }

    private void initialize() {
        number = preferences.getInt("No", 0);
        presenter = new HostPresenter(this, number);
        mBuilder = new AlertDialog.Builder(getContext());
        adapter = new MoviesAdapter(getContext());
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        //setting recycler view
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setToolBar() {
        ActionBar toolbar = ((MainActivity)getActivity()).getSupportActionBar();
        toolbar.setTitle(R.string.app_name);
        toolbar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            showChangeMoviesOptions();
            return true;
        }
        if (id == R.id.goToMvvm){
            Navigation.findNavController(layout).navigate(R.id.action_blankFragment_to_blankFragment2, bundle);
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData(){
        HostViewModel viewModel = ViewModelProviders.of(this).get(HostViewModel.class);
        viewModel.getMovies(getContext()).observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                adapter.addAll(movies);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void loadNextPage(){
    //   presenter.loadNext();
        Service movieService = Connector.getConnector(getContext()).create(Service.class);
        Call<MoviesResponse> call = movieService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN, currentPage);
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                adapter.removeLoadingFooter();
                isLoading = false;
                List<Movie> results = response.body().getResults();
                adapter.addAll(results);
                adapter.notifyDataSetChanged();

                if (currentPage != TOTAL_PAGES)adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {}
        });
    }

    private void refreshPage(){
        refreshLayout.setColorScheme(R.color.black, R.color.colorAccent, R.color.colorPrimary, R.color.lightGray);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getData();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showChangeMoviesOptions() {
        mBuilder.setTitle("Sort movies by");
        mBuilder.setSingleChoiceItems(moviesOptions, number, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){
                    setMovies(String.valueOf(i));
                }else if (i==1) {
                    setMovies(String.valueOf(i));
                }
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void setMovies(String i) {
        Locale locale = new Locale(i);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
       getActivity().getBaseContext().getResources().updateConfiguration(configuration, getActivity()
               .getBaseContext().getResources().getDisplayMetrics());

//        save data to shared preferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("No", Integer.parseInt(i));
        editor.apply();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        refreshLayout.setRefreshing(false);
       // adapter.removeLoadingFooter();
    }

    @Override
    public void showResults(List<Movie> moveData) {
        adapter.addAll(moveData);
        adapter.notifyDataSetChanged();
        recyclerView.addOnScrollListener(new MoviesScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                //network delay for calling api
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }
        });

    }

    @Override
    public void onErrorLoading(String message) {
        Toast.makeText(getContext(), "Error " + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoadingFirstPage(boolean firstPage) {
        if(firstPage){
            adapter.addLoadingFooter();
        }if (!firstPage){
            isLastPage = true;
        }
    }

    @Override
    public void hideLoading(boolean b) {
        if (b){ adapter.removeLoadingFooter();}
        if (currentPage != TOTAL_PAGES)adapter.addLoadingFooter();
        else isLastPage = true;
    }

    private void goToMVVM(String title, String posterPath, String overview) {
        bundle.putString("title", title);
        bundle.putString("path", posterPath);
        bundle.putString("overview", overview);
    }
}
