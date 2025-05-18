import SearchInput from "@/components/shared/SearchInput";
import BookTable from "@/components/table/BookTable";
import React, { useState } from "react";

const Book = () => {
  const [searchValue, setSearchValue] = useState<string>("");

  const handleSearchQueryChange = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setSearchValue(event.target.value);
  };
  return (
    <div className="bg-white px-10 py-6 rounded-lg">
      <div className="flex flex-1 sm:flex-row flex-col gap-y-4 justify-between pb-6">
        <div>
          <h1 className="text-base font-medium">Manage books</h1>
          <p className="text-gray-500 text-[14px]">
            Extract and manipulate the books over here!
          </p>
        </div>
        <div className="flex flex-row gap-x-2">
          <SearchInput searchQueryValue={searchValue} handleSearchQueryValue={handleSearchQueryChange} />
        </div>
      </div>
      <BookTable searchQuery={searchValue} />
    </div>
  );
};

export default Book;
